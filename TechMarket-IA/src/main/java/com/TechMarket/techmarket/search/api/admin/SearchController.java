package com.techmarket.techmarket.search.api.admin;

import com.techmarket.techmarket.search.api.admin.request.CreateSearchHistoryRequest;
import com.techmarket.techmarket.search.api.admin.response.CreateSearchHistoryResponse;
import com.techmarket.techmarket.search.api.admin.response.GlobalSearchItemResponse;
import com.techmarket.techmarket.search.api.admin.response.GlobalSearchResponse;
import com.techmarket.techmarket.search.api.admin.response.SearchHistoryResponse;
import com.techmarket.techmarket.search.api.admin.response.SearchMessageResponse;
import com.techmarket.techmarket.search.api.admin.response.SearchSuggestionResponse;
import com.techmarket.techmarket.search.api.admin.response.SearchTrendingResponse;
import com.techmarket.techmarket.search.application.service.SearchIdentitySupport;
import com.techmarket.techmarket.search.infrastructure.persistence.jdbc.SearchJdbcRepository;
import com.techmarket.techmarket.search.infrastructure.persistence.jpa.entity.SearchHistoryJpaEntity;
import com.techmarket.techmarket.search.infrastructure.persistence.jpa.entity.SearchTrendJpaEntity;
import com.techmarket.techmarket.search.infrastructure.persistence.jpa.repository.SearchHistorySpringDataRepository;
import com.techmarket.techmarket.search.infrastructure.persistence.jpa.repository.SearchTrendSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchIdentitySupport identitySupport;
    private final SearchJdbcRepository searchRepository;
    private final SearchHistorySpringDataRepository historyRepository;
    private final SearchTrendSpringDataRepository trendRepository;

    public SearchController(
            SearchIdentitySupport identitySupport,
            SearchJdbcRepository searchRepository,
            SearchHistorySpringDataRepository historyRepository,
            SearchTrendSpringDataRepository trendRepository) {
        this.identitySupport = identitySupport;
        this.searchRepository = searchRepository;
        this.historyRepository = historyRepository;
        this.trendRepository = trendRepository;
    }

    @GetMapping("/global")
    public GlobalSearchResponse global(@RequestParam(value = "q", required = false) String query) {
        String normalized = normalizeQuery(query);
        List<GlobalSearchItemResponse> results =
                normalized.isBlank() ? List.of() : searchRepository.search(normalized);
        return new GlobalSearchResponse(results.size(), results);
    }

    @GetMapping("/suggestions")
    public List<SearchSuggestionResponse> suggestions(
            @RequestParam(value = "q", required = false) String query) {
        String normalized = normalizeQuery(query);
        if (normalized.isBlank()) {
            return trendRepository.findTop10ByOrderBySearchCountDescUpdatedAtDesc().stream()
                    .map(trend -> new SearchSuggestionResponse(trend.getQueryText(), "termino"))
                    .toList();
        }
        return searchRepository.suggestions(normalized).stream()
                .map(text -> new SearchSuggestionResponse(text, "termino"))
                .toList();
    }

    @GetMapping("/trending")
    public List<SearchTrendingResponse> trending() {
        List<SearchTrendingResponse> persisted =
                trendRepository.findTop10ByOrderBySearchCountDescUpdatedAtDesc().stream()
                        .map(
                                trend ->
                                        new SearchTrendingResponse(
                                                trend.getQueryText(), trend.getSearchCount()))
                        .toList();
        if (!persisted.isEmpty()) {
            return persisted;
        }
        return List.of(
                new SearchTrendingResponse("mantenimiento laptop", 184),
                new SearchTrendingResponse("monitor ultrawide", 142));
    }

    @GetMapping("/history")
    public List<SearchHistoryResponse> history(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return historyRepository.findAllByUserIdOrderBySearchedAtDesc(currentUserId).stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    @PostMapping("/history")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSearchHistoryResponse saveHistory(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSearchHistoryRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SearchHistoryJpaEntity history = new SearchHistoryJpaEntity();
        history.setId(UUID.randomUUID());
        history.setUserId(currentUserId);
        history.setQueryText(request.query());
        history.setResultType(request.tipo());
        history.setSearchedAt(OffsetDateTime.now());
        SearchHistoryJpaEntity saved = historyRepository.save(history);
        incrementTrend(request.query());
        return new CreateSearchHistoryResponse(formatHistoryId(saved.getId()), "Busqueda guardada");
    }

    @DeleteMapping("/history/{historyId}")
    public SearchMessageResponse deleteHistory(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String historyId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(historyId, "SRH-");
        SearchHistoryJpaEntity history =
                historyRepository
                        .findByIdAndUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Search history not found"));
        historyRepository.delete(history);
        return new SearchMessageResponse("Busqueda eliminada del historial");
    }

    private SearchHistoryResponse toHistoryResponse(SearchHistoryJpaEntity history) {
        return new SearchHistoryResponse(
                formatHistoryId(history.getId()), history.getQueryText(), history.getSearchedAt());
    }

    private void incrementTrend(String query) {
        String normalized = normalizeQuery(query);
        SearchTrendJpaEntity trend =
                trendRepository
                        .findByQueryTextIgnoreCase(normalized)
                        .orElseGet(
                                () -> {
                                    SearchTrendJpaEntity created = new SearchTrendJpaEntity();
                                    created.setId(UUID.randomUUID());
                                    created.setQueryText(normalized);
                                    return created;
                                });
        trend.setSearchCount(trend.getSearchCount() + 1);
        trend.setUpdatedAt(OffsetDateTime.now());
        trendRepository.save(trend);
    }

    private String normalizeQuery(String query) {
        return query == null ? "" : query.trim();
    }

    private String formatHistoryId(UUID id) {
        return "SRH-" + id;
    }
}
