package com.techmarket.techmarket.search.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSearchHistoryRequest(
        @NotBlank(message = "query is required") String query,
        String tipo) {}
