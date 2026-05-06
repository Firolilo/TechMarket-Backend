package com.techmarket.techmarket.users.api.admin.client.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(String id, BigDecimal subtotal, List<CartItemResponse> items) {}
