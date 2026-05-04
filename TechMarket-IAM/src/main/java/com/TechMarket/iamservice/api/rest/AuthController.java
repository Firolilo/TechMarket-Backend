package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.application.dto.AuthUserSummaryResponse;
import com.techmarket.iamservice.application.dto.AuthTokenResponse;
import com.techmarket.iamservice.application.dto.ForgotPasswordRequest;
import com.techmarket.iamservice.application.dto.LoginRequest;
import com.techmarket.iamservice.application.dto.LoginEndpointResponse;
import com.techmarket.iamservice.application.dto.LogoutRequest;
import com.techmarket.iamservice.application.dto.MessageResponse;
import com.techmarket.iamservice.application.dto.RefreshTokenRequest;
import com.techmarket.iamservice.application.dto.RefreshEndpointResponse;
import com.techmarket.iamservice.application.dto.RegisterEndpointResponse;
import com.techmarket.iamservice.application.dto.RegisterRequest;
import com.techmarket.iamservice.application.dto.VerifyOtpRequest;
import com.techmarket.iamservice.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth", description = "Authentication and token operations")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @Valid @RequestBody LoginRequest request) {
        AuthTokenResponse response = authService.login(tenantId, request);
        if (!request.endpointContract() || response.otpRequired()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(toLoginEndpointResponse(response));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @Valid @RequestBody RegisterRequest request) {
        AuthTokenResponse response = authService.register(tenantId, request);
        if (!request.endpointContract() || response.otpRequired()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new RegisterEndpointResponse(
                                formatUserId(response.userId()),
                                request.email(),
                                request.nombre(),
                                request.tipo(),
                                "Pendiente verificacion",
                                "Verificar email enviado a " + request.email()));
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<AuthTokenResponse> verifyOtp(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(tenantId, request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<RefreshEndpointResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthTokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(
                new RefreshEndpointResponse(
                        response.accessToken(), response.refreshToken(), response.expiresIn()));
    }

    @PostMapping("/auth/logout")
    @Operation(security = {@SecurityRequirement(name = "bearer-jwt")})
    public ResponseEntity<MessageResponse> logout(
            Authentication authentication,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
                    String authorizationHeader,
            @Valid @RequestBody(required = false) LogoutRequest request) {
        authService.logout(
                request == null ? null : new RefreshTokenRequest(request.refreshToken()),
                authentication,
                authorizationHeader);
        return ResponseEntity.ok(new MessageResponse("Sesion cerrada exitosamente", Instant.now()));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(
                new MessageResponse(
                        "Instrucciones de recuperacion enviadas si el email existe",
                        Instant.now()));
    }

    private LoginEndpointResponse toLoginEndpointResponse(AuthTokenResponse response) {
        return new LoginEndpointResponse(
                new AuthUserSummaryResponse(
                        formatUserId(response.userId()),
                        null,
                        response.username(),
                        resolveTipo(response),
                        "Activo"),
                response.accessToken(),
                response.refreshToken(),
                response.expiresIn());
    }

    private String resolveTipo(AuthTokenResponse response) {
        if (response.roles() == null || response.roles().isEmpty()) {
            return "cliente";
        }
        return response.roles().get(0).toLowerCase();
    }

    private String formatUserId(Long userId) {
        return userId == null ? null : "USR-" + String.format("%03d", userId);
    }
}
