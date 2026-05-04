package com.techmarket.techmarket.marketplace.api.admin.response;

import java.util.List;

public record CategoryTreeResponse(
        String id, String nombre, List<CategoryTreeResponse> subcategorias) {}
