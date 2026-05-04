package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "email is required") @Email(message = "email is invalid") String email,
        @NotBlank(message = "password is required")
                @Size(min = 8, max = 72, message = "password must have 8-72 chars")
                String password,
        @NotBlank(message = "confirmPassword is required") String confirmPassword,
        @NotBlank(message = "tipo is required") @Size(max = 50, message = "tipo is too long")
                String tipo,
        @NotBlank(message = "nombre is required") @Size(max = 100, message = "nombre is too long")
                String nombre,
        @NotBlank(message = "apellido is required")
                @Size(max = 100, message = "apellido is too long")
                String apellido,
        @NotBlank(message = "telefono is required")
                @Size(max = 30, message = "telefono is too long")
                String telefono,
        @NotBlank(message = "pais is required") @Size(max = 100, message = "pais is too long")
                String pais,
        @NotBlank(message = "ciudad is required") @Size(max = 100, message = "ciudad is too long")
                String ciudad,
        @AssertTrue(message = "terminos must be accepted") boolean terminos) {}
