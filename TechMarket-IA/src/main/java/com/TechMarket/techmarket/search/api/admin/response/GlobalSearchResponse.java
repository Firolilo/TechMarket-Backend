package com.techmarket.techmarket.search.api.admin.response;

import java.util.List;

public record GlobalSearchResponse(int total, List<GlobalSearchItemResponse> resultados) {}
