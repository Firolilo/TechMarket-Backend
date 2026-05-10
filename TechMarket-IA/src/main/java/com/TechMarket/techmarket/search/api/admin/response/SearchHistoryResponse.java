package com.techmarket.techmarket.search.api.admin.response;

import java.time.OffsetDateTime;

public record SearchHistoryResponse(String id, String query, OffsetDateTime fecha) {}
