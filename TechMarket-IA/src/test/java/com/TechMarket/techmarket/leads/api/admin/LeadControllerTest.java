package com.techmarket.techmarket.leads.api.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.leads.application.service.LeadApplicationService;
import com.techmarket.techmarket.leads.domain.model.Lead;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LeadController.class)
class LeadControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private LeadApplicationService service;

    @Test
    void create_shouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID listingId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        when(service.create(any()))
                .thenReturn(
                        new Lead(
                                id,
                                tenantId,
                                null,
                                userId,
                                listingId,
                                "CHAT",
                                "OPEN",
                                10,
                                "Hola, me interesa el equipo",
                                now,
                                now));

        String payload =
                """
                {
                  "tenantId": "%s",
                  "userId": "%s",
                  "listingId": "%s",
                  "sourceChannel": "CHAT",
                  "status": "OPEN",
                  "leadScore": 10,
                  "notes": "Hola, me interesa el equipo"
                }
                """
                        .formatted(tenantId, userId, listingId);

        mockMvc.perform(
                        post("/api/admin/leads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.sourceChannel").value("CHAT"));
    }
}
