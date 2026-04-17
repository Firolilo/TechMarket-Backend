package com.techmarket.techmarket.tickets.api.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.tickets.application.service.TicketApplicationService;
import com.techmarket.techmarket.tickets.domain.model.Ticket;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TicketApplicationService service;

    @Test
    void create_shouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID listingId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        when(service.create(any()))
                .thenReturn(
                        new Ticket(
                                id,
                                "TCK-001",
                                tenantId,
                                customerId,
                                listingId,
                                "No enciende",
                                "HIGH",
                                "OPEN",
                                now,
                                now));

        String payload =
                """
                {
                  "ticketCode": "TCK-001",
                  "tenantId": "%s",
                  "customerUserId": "%s",
                  "listingId": "%s",
                  "subject": "No enciende",
                  "priority": "HIGH",
                  "status": "OPEN"
                }
                """
                        .formatted(tenantId, customerId, listingId);

        mockMvc.perform(
                        post("/api/admin/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.ticketCode").value("TCK-001"));
    }
}
