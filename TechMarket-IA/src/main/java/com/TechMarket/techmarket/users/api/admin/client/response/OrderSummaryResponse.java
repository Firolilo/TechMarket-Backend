package com.techmarket.techmarket.users.api.admin.client.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderSummaryResponse(
        String id, OffsetDateTime fechaCreacion, String estado, BigDecimal total) {}
