package com.techmarket.ai.api.controller;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import com.techmarket.ai.application.port.in.MarketplaceSearchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Semantic marketplace discovery: a client searches in natural language and gets the companies /
 * specialists whose offerings match by meaning (not keyword). Indexing is fed from the real catalog
 * in TechMarket-IA.
 */
@RestController
@RequestMapping("/api/v1/ai/marketplace")
@Tag(
        name = "Marketplace Search",
        description = "Semantic (RAG) search over the marketplace catalog")
public class MarketplaceSearchController {

    private final MarketplaceSearchUseCase useCase;

    public MarketplaceSearchController(MarketplaceSearchUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/buscar")
    @Operation(summary = "Natural-language semantic search of the marketplace")
    public ResponseEntity<List<MarketplaceHitDto>> buscar(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(useCase.search(request.query(), request.topK()));
    }

    @PostMapping("/index")
    @Operation(
            summary = "Re-index the marketplace catalog (clears and upserts the given documents)")
    public ResponseEntity<IndexResponse> index(@RequestBody IndexRequest request) {
        int count = useCase.reindex(request.documentos());
        return ResponseEntity.ok(new IndexResponse(count));
    }

    /** {@code query} = natural-language text; {@code topK} optional. */
    public record SearchRequest(String query, Integer topK) {}

    public record IndexRequest(List<MarketplaceDocDto> documentos) {}

    public record IndexResponse(int indexed) {}
}
