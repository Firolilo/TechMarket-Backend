package com.techmarket.techmarket.leads.api.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
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
class LeadIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void create_shouldPersistAndReturnCreated() throws Exception {
        String payload =
                """
                {
                  "tenantId": "%s",
                  "userId": "%s",
                  "listingId": "%s",
                  "sourceChannel": "CHAT",
                  "status": "OPEN",
                  "leadScore": 5,
                  "notes": "Quiero cotizar por WhatsApp"
                }
                """
                        .formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(
                        post("/api/admin/leads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }
}
