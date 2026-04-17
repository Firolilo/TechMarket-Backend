package com.techmarket.techmarket.tickets.api.admin;

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
class TicketIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void create_shouldPersistAndReturnCreated() throws Exception {
        String payload =
                """
                {
                  "ticketCode": "TCK-900",
                  "tenantId": "%s",
                  "customerUserId": "%s",
                  "listingId": "%s",
                  "subject": "Pantalla azul",
                  "priority": "MEDIUM",
                  "status": "OPEN"
                }
                """
                        .formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(
                        post("/api/admin/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.subject").value("Pantalla azul"));
    }
}
