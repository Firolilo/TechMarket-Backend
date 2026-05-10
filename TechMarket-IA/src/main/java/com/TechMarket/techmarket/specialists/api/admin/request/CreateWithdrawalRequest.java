package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateWithdrawalRequest(@NotNull(message = "monto is required") BigDecimal monto) {}
