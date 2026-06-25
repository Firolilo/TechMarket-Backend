package com.techmarket.ai.infrastructure.vector.pgvector;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import com.techmarket.ai.application.port.out.MarketplaceIndexPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * Marketplace semantic index backed by a Spring AI {@link VectorStore} (PgVector in docker/prod,
 * SimpleVectorStore in dev). The store embeds text via the configured EmbeddingModel; we tag every
 * document with {@code namespace=marketplace} so the catalog is isolated from RAG-QA content.
 *
 * <p>Wired conditionally in {@code MarketplaceIndexConfig} (only when a {@link VectorStore} bean
 * exists); otherwise the stub adapter keeps the endpoint responsive.
 */
public class MarketplaceVectorIndexAdapter implements MarketplaceIndexPort {

    private static final String NAMESPACE = "marketplace";

    private final VectorStore vectorStore;

    public MarketplaceVectorIndexAdapter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void index(List<MarketplaceDocDto> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        List<Document> docs =
                documents.stream()
                        .map(
                                d -> {
                                    Map<String, Object> meta = new HashMap<>();
                                    meta.put("namespace", NAMESPACE);
                                    meta.put("itemId", String.valueOf(d.id()));
                                    meta.put("type", String.valueOf(d.type()));
                                    meta.put("title", String.valueOf(d.title()));
                                    meta.put("ownerName", String.valueOf(d.ownerName()));
                                    return Document.builder()
                                            .id(d.id())
                                            .text(d.content() == null ? "" : d.content())
                                            .metadata(meta)
                                            .build();
                                })
                        .collect(Collectors.toList());
        vectorStore.add(docs);
    }

    @Override
    public void clear() {
        try {
            vectorStore.delete(new FilterExpressionBuilder().eq("namespace", NAMESPACE).build());
        } catch (RuntimeException ex) {
            // Some stores don't support filter-delete; reindex still upserts by stable id.
        }
    }

    @Override
    public List<MarketplaceHitDto> search(String query, int topK) {
        SearchRequest request =
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThresholdAll()
                        .filterExpression(
                                new FilterExpressionBuilder().eq("namespace", NAMESPACE).build())
                        .build();
        List<Document> docs = vectorStore.similaritySearch(request);
        if (docs == null) {
            return List.of();
        }
        return docs.stream().map(this::toHit).collect(Collectors.toList());
    }

    private MarketplaceHitDto toHit(Document doc) {
        Map<String, Object> meta = doc.getMetadata();
        Double score = doc.getScore();
        return new MarketplaceHitDto(
                str(meta.get("itemId")),
                str(meta.get("type")),
                str(meta.get("title")),
                str(meta.get("ownerName")),
                score == null ? 0.0 : score);
    }

    private String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
