package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.AuthTokenResponse;
import com.techmarket.iamservice.application.dto.RegisterUserRequest;
import com.techmarket.iamservice.application.service.AuthService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRegisterContractTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;

    @Test
    void shouldRegisterUserAndReturnTokens() throws Exception {
        RegisterUserRequest request =
                new RegisterUserRequest(
                        "usuario@example.com",
                        "SecurePass123!",
                        "SecurePass123!",
                        "cliente",
                        "Juan",
                        "Pérez",
                        "+56912345678",
                        "Bolivia",
                        "Santa Cruz",
                        true);

        when(authService.register(any()))
                .thenReturn(
                        new AuthTokenResponse(
                                "access-token",
                                "refresh-token",
                                "Bearer",
                                900,
                                604800,
                                "00000000-0000-0000-0000-000000000000",
                                77L,
                                "usuario@example.com",
                                List.of(),
                                List.of()));

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.tenantId").value("00000000-0000-0000-0000-000000000000"))
                .andExpect(jsonPath("$.userId").value(77L))
                .andExpect(jsonPath("$.username").value("usuario@example.com"));
    }

    @Test
    void shouldRejectInvalidRegistrationPayload() throws Exception {
        String requestBody =
                """
                {
                  "email": "usuario@example.com",
                  "password": "SecurePass123!",
                                                                        "confirmPassword": "SecurePass123!",
                  "tipo": "cliente",
                  "nombre": "Juan",
                  "apellido": "Pérez",
                  "telefono": "+56912345678",
                  "pais": "Bolivia",
                  "ciudad": "Santa Cruz",
                                                                        "terminos": false
                }
                """;

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.path").value("/auth/register"));
    }
}
