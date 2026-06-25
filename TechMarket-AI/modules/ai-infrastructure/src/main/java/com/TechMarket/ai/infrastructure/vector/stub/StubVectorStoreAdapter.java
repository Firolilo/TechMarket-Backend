package com.techmarket.ai.infrastructure.vector.stub;

import com.techmarket.ai.application.dto.RagChunkDto;
import com.techmarket.ai.application.port.out.VectorStorePort;
import java.util.List;

/**
 * Stub VectorStorePort used as a deterministic fallback when no Spring AI {@code VectorStore} is
 * available (no real embeddings/pgvector). Returns empty results so the AI service still boots and
 * the RAG endpoints respond. Wired in {@code VectorStoreConfig} via
 * {@code @ConditionalOnMissingBean}.
 */
public class StubVectorStoreAdapter implements VectorStorePort {

    @Override
    public List<RagChunkDto> similaritySearch(
            String query, int topK, String tenantId, String namespace) {
        return List.of();
    }
}
