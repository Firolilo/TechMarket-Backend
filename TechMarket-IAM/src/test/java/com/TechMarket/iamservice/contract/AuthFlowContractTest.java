package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.application.service.OtpDeliveryService;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserCredentialEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantUserRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserCredentialRepository;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowContractTest {

    private static final String TENANT_ID = "77777777-7777-7777-7777-777777777777";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TenantUserRepository tenantUserRepository;
    @Autowired private UserCredentialRepository userCredentialRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @MockBean private OtpDeliveryService otpDeliveryService;

    @Test
    void shouldRegisterAndVerifyOtp() throws Exception {
        AtomicReference<String> deliveredOtp = new AtomicReference<>();
        doAnswer(
                        invocation -> {
                            deliveredOtp.set(invocation.getArgument(2, String.class));
                            return null;
                        })
                .when(otpDeliveryService)
                .deliver(anyString(), any(UserEntity.class), anyString(), anyString());

        MvcResult registerResult =
                mockMvc.perform(
                                post("/auth/register")
                                        .header("X-Tenant-Id", TENANT_ID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        Map.of(
                                                                "username", "buyer.otp",
                                                                "email", "buyer.otp@example.com",
                                                                "password", "StrongPass123"))))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.otpRequired").value(true))
                        .andExpect(jsonPath("$.tokenType").value("OTP"))
                        .andExpect(jsonPath("$.accessToken").doesNotExist())
                        .andReturn();

        String challengeId =
                objectMapper
                        .readTree(registerResult.getResponse().getContentAsString())
                        .get("otpChallengeId")
                        .asText();

        mockMvc.perform(
                        post("/auth/verify-otp")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "username",
                                                        "buyer.otp",
                                                        "otpChallengeId",
                                                        challengeId,
                                                        "otpCode",
                                                        deliveredOtp.get()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otpRequired").value(false))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    void shouldRequireOtpOnLoginWhenEnabledForUser() throws Exception {
        UserEntity user = new UserEntity("otp.login", "otp.login@example.com", true);
        user.setTenantId(TENANT_ID);
        UserEntity savedUser = tenantUserRepository.save(user);

        UserCredentialEntity credential =
                new UserCredentialEntity(
                        savedUser.getId(), TENANT_ID, passwordEncoder.encode("StrongPass123"));
        credential.setOtpEnabled(true);
        userCredentialRepository.save(credential);

        mockMvc.perform(
                        post("/auth/login")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "username", "otp.login",
                                                        "password", "StrongPass123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otpRequired").value(true))
                .andExpect(jsonPath("$.tokenType").value("OTP"))
                .andExpect(jsonPath("$.otpChallengeId").isString());
    }
}
