package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.AuthTokenResponse;
import com.techmarket.iamservice.application.dto.LoginRequest;
import com.techmarket.iamservice.application.dto.RefreshTokenRequest;
import com.techmarket.iamservice.application.exception.AuthServiceException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerContractTest {

    private static final String TENANT_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private com.techmarket.iamservice.application.service.AuthService authService;

    @Test
    void shouldReturn200WhenLoginSuccessful() throws Exception {
        AuthTokenResponse response =
                new AuthTokenResponse(
                        "access-token",
                        "refresh-token",
                        "Bearer",
                        900,
                        604800,
                        TENANT_ID,
                        1L,
                        "admin",
                        List.of("ADMIN"),
                        List.of("iam.users.read"));
        when(authService.login(anyString(), any(LoginRequest.class))).thenReturn(response);

        LoginRequest request = new LoginRequest("admin", "password123");
        mockMvc.perform(
                        post("/auth/login")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void shouldReturn401WhenInvalidCredentials() throws Exception {
        when(authService.login(anyString(), any(LoginRequest.class)))
                .thenThrow(
                        new AuthServiceException(
                                ErrorCodes.IAM_INVALID_CREDENTIALS,
                                HttpStatus.UNAUTHORIZED,
                                "Invalid credentials"));

        LoginRequest request = new LoginRequest("admin", "wrong");
        mockMvc.perform(
                        post("/auth/login")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.IAM_INVALID_CREDENTIALS))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn400WhenLoginWithBlankUsername() throws Exception {
        String requestBody =
                """
                {"username": "", "password": "password123"}
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void shouldReturn200WhenRefreshSuccessful() throws Exception {
        AuthTokenResponse response =
                new AuthTokenResponse(
                        "new-access",
                        "new-refresh",
                        "Bearer",
                        900,
                        604800,
                        TENANT_ID,
                        1L,
                        "admin",
                        List.of("ADMIN"),
                        List.of());
        when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

        String requestBody =
                """
                {"refreshToken": "valid-refresh-token"}
                """;

        mockMvc.perform(
                        post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    void shouldReturn401WhenRefreshTokenInvalid() throws Exception {
        when(authService.refresh(any(RefreshTokenRequest.class)))
                .thenThrow(
                        new AuthServiceException(
                                ErrorCodes.IAM_INVALID_REFRESH_TOKEN,
                                HttpStatus.UNAUTHORIZED,
                                "Invalid"));

        String requestBody =
                """
                {"refreshToken": "invalid-token"}
                """;

        mockMvc.perform(
                        post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.IAM_INVALID_REFRESH_TOKEN));
    }
}
