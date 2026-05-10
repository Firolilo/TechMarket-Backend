package com.techmarket.techmarket.ambassadors.api.admin.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateAmbassadorWithdrawalRequest(@NotNull @Positive BigDecimal monto) {}
