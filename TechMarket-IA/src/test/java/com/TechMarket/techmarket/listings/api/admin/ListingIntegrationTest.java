package com.techmarket.techmarket.listings.api.admin;

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
class ListingIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void create_shouldPersistAndReturnCreated() throws Exception {
        String payload =
                """
                {
                  "tenantId": "%s",
                  "categoryId": "%s",
                  "brandId": "%s",
                  "title": "Mainboard X670E",
                  "basePrice": 389.50,
                  "currency": "USD",
                  "status": "ACTIVE"
                }
                """
                        .formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(
                        post("/api/admin/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Mainboard X670E"));
    }
}
