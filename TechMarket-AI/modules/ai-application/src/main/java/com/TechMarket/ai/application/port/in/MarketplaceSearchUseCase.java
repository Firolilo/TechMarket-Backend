package com.techmarket.ai.application.port.in;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import java.util.List;

/** Semantic discovery of the marketplace: index the catalog and search it by natural language. */
public interface MarketplaceSearchUseCase {

    /**
     * Re-indexes the given catalog (clears then upserts). Returns how many documents were indexed.
     */
    int reindex(List<MarketplaceDocDto> documents);

    /** Natural-language search; {@code topK} defaults to a sane value when null or out of range. */
    List<MarketplaceHitDto> search(String query, Integer topK);
}
