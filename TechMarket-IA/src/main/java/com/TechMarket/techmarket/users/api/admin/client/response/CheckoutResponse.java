package com.techmarket.techmarket.users.api.admin.client.response;

import java.math.BigDecimal;

public record CheckoutResponse(String ordenId, String estado, BigDecimal total) {}
