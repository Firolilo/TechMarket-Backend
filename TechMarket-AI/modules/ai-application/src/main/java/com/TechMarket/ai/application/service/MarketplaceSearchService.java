package com.techmarket.ai.application.service;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import com.techmarket.ai.application.port.in.MarketplaceSearchUseCase;
import com.techmarket.ai.application.port.out.AuditPort;
import com.techmarket.ai.application.port.out.MarketplaceIndexPort;
import java.util.List;
import java.util.Map;

/** Semantic marketplace discovery backed by an embedding-powered vector index. */
public class MarketplaceSearchService implements MarketplaceSearchUseCase {

    private static final int DEFAULT_TOP_K = 8;
    private static final int MAX_TOP_K = 50;

    private final MarketplaceIndexPort index;
    private final AuditPort audit;

    public MarketplaceSearchService(MarketplaceIndexPort index, AuditPort audit) {
        this.index = index;
        this.audit = audit;
    }

    @Override
    public int reindex(List<MarketplaceDocDto> documents) {
        List<MarketplaceDocDto> docs = documents == null ? List.of() : documents;
        index.clear();
        if (!docs.isEmpty()) {
            index.index(docs);
        }
        audit.audit("marketplace.search.reindex", Map.of("count", docs.size()));
        return docs.size();
    }

    @Override
    public List<MarketplaceHitDto> search(String query, Integer topK) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        int k = topK == null || topK <= 0 ? DEFAULT_TOP_K : Math.min(topK, MAX_TOP_K);
        List<MarketplaceHitDto> hits = index.search(query.trim(), k);
        audit.audit("marketplace.search.query", Map.of("k", k, "hits", hits.size()));
        return hits;
    }
}
