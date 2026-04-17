package com.techmarket.techmarket.tenants.api.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TenantIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void create_shouldPersistAndReturnCreated() throws Exception {
        String payload =
                """
                {
                  "businessName": "CircuitLabs",
                  "legalName": "CircuitLabs SA",
                  "taxId": "CL-123",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/admin/tenants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.businessName").value("CircuitLabs"));
    }
}
