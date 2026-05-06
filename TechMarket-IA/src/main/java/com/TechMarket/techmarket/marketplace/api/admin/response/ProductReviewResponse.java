package com.techmarket.techmarket.marketplace.api.admin.response;

import java.time.LocalDate;

public record ProductReviewResponse(
        String id,
        ReviewCustomerResponse cliente,
        Integer calificacion,
        String comentario,
        LocalDate fecha) {}
