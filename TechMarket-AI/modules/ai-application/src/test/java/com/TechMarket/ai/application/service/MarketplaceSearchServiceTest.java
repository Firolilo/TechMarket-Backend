package com.techmarket.ai.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import com.techmarket.ai.application.port.out.MarketplaceIndexPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Pure (offline) verification of the marketplace search logic against a fake, keyword-matching
 * index port. Real semantic ranking needs a live embedding model + vector store; this proves the
 * use-case contract: reindex clears+stores, and search delegates with sane top-k handling.
 */
class MarketplaceSearchServiceTest {

    private final List<MarketplaceDocDto> store = new ArrayList<>();
    private final FakeIndex index = new FakeIndex();
    private final MarketplaceSearchService service =
            new MarketplaceSearchService(index, (action, data) -> {});

    @Test
    void reindex_clearsThenStores_andReturnsCount() {
        store.add(new MarketplaceDocDto("X", "empresa", "x", "Acme", "viejo"));

        int n =
                service.reindex(
                        List.of(
                                new MarketplaceDocDto(
                                        "SVC-1",
                                        "especialista",
                                        "Reparación de laptops",
                                        "Juan",
                                        "Reparo laptops y recupero datos"),
                                new MarketplaceDocDto(
                                        "SVC-2",
                                        "especialista",
                                        "Redes",
                                        "Ana",
                                        "Configuro redes LAN y switches")));

        assertEquals(2, n);
        assertTrue(index.cleared);
        assertEquals(2, store.size()); // old doc gone (cleared), 2 new
    }

    @Test
    void search_blankQuery_returnsEmpty_withoutHittingIndex() {
        assertTrue(service.search("  ", 5).isEmpty());
    }

    @Test
    void search_delegatesAndReturnsHits() {
        service.reindex(
                List.of(
                        new MarketplaceDocDto(
                                "SVC-2", "especialista", "Redes", "Ana", "configuro redes lan")));

        List<MarketplaceHitDto> hits = service.search("redes", 5);

        assertEquals(1, hits.size());
        assertEquals("SVC-2", hits.get(0).id());
    }

    /** In-memory fake: naive substring match so the use-case wiring is testable offline. */
    private final class FakeIndex implements MarketplaceIndexPort {
        private boolean cleared;

        @Override
        public void index(List<MarketplaceDocDto> documents) {
            store.addAll(documents);
        }

        @Override
        public void clear() {
            cleared = true;
            store.clear();
        }

        @Override
        public List<MarketplaceHitDto> search(String query, int topK) {
            String q = query.toLowerCase(Locale.ROOT);
            return store.stream()
                    .filter(
                            d ->
                                    (d.title() + " " + d.content())
                                            .toLowerCase(Locale.ROOT)
                                            .contains(q))
                    .limit(topK)
                    .map(
                            d ->
                                    new MarketplaceHitDto(
                                            d.id(), d.type(), d.title(), d.ownerName(), 1.0))
                    .toList();
        }
    }
}
