package com.techmarket.ai.infrastructure.vector.stub;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import com.techmarket.ai.application.port.out.MarketplaceIndexPort;
import java.util.List;

/**
 * Fallback marketplace index used when no real {@link
 * org.springframework.ai.vectorstore.VectorStore} is configured (no embeddings provider). Keeps the
 * search endpoint responsive (empty results) instead of failing context startup. Wired in {@code
 * MarketplaceIndexConfig} only when no other {@link MarketplaceIndexPort} bean exists.
 */
public class StubMarketplaceIndexAdapter implements MarketplaceIndexPort {

    @Override
    public void index(List<MarketplaceDocDto> documents) {
        // no-op
    }

    @Override
    public void clear() {
        // no-op
    }

    @Override
    public List<MarketplaceHitDto> search(String query, int topK) {
        return List.of();
    }
}
