package com.techmarket.techmarket.users.api.admin.client.response;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailResponse(
        String id,
        String estado,
        BigDecimal total,
        List<OrderItemResponse> items,
        TrackingResponse tracking) {}
