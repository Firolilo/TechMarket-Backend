package com.techmarket.techmarket.marketplace.api.admin.response;

import java.util.List;

public record ProductPageResponse(int total, int pagina, List<ProductSummaryResponse> productos) {}
