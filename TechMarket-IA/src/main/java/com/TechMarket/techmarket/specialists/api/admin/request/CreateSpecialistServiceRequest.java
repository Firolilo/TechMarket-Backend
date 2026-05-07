package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateSpecialistServiceRequest(
        @NotBlank(message = "nombre is required")
                @Size(max = 255, message = "nombre must have at most 255 chars")
                String nombre,
        @Size(max = 255, message = "descripcion must have at most 255 chars")
                String descripcion,
        @NotNull(message = "precio is required")
                @DecimalMin(value = "0.00", message = "precio must be positive")
                BigDecimal precio,
        @Size(max = 16, message = "moneda must have at most 16 chars") String moneda,
        @NotBlank(message = "tipo is required")
                @Size(max = 255, message = "tipo must have at most 255 chars")
                String tipo,
        Boolean destacado) {}
