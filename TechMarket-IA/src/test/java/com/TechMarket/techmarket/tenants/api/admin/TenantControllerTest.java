package com.techmarket.techmarket.tenants.api.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.tenants.application.service.TenantApplicationService;
import com.techmarket.techmarket.tenants.domain.model.Tenant;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TenantController.class)
class TenantControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TenantApplicationService service;

    @Test
    void create_shouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        when(service.create(any()))
                .thenReturn(
                        new Tenant(
                                id,
                                "TechMarket HQ",
                                "TechMarket LLC",
                                "TAX-001",
                                "ACTIVE",
                                now,
                                now));

        String payload =
                """
                {
                  "businessName": "TechMarket HQ",
                  "legalName": "TechMarket LLC",
                  "taxId": "TAX-001",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/admin/tenants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.taxId").value("TAX-001"));
    }
}
