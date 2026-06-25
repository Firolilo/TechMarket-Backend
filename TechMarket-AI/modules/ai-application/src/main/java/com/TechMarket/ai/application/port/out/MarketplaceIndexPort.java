package com.techmarket.ai.application.port.out;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import java.util.List;

/**
 * Port out: semantic index of the marketplace catalog. Unlike {@link VectorStorePort} (tenant-
 * scoped RAG QA), marketplace search is cross-tenant: a client discovers offerings from any company
 * or specialist by meaning. The owner is kept as metadata on each hit, not used as a filter.
 */
public interface MarketplaceIndexPort {

    /** Upserts catalog documents into the marketplace vector index (embedded by the adapter). */
    void index(List<MarketplaceDocDto> documents);

    /** Removes everything currently in the marketplace index (used before a full re-ingest). */
    void clear();

    /** Semantic similarity search across the whole marketplace; returns the top-k closest hits. */
    List<MarketplaceHitDto> search(String query, int topK);
}
