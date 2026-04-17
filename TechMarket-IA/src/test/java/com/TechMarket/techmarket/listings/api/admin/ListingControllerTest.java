package com.techmarket.techmarket.listings.api.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.listings.application.service.ListingApplicationService;
import com.techmarket.techmarket.listings.domain.model.Listing;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ListingApplicationService service;

    @Test
    void create_shouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        when(service.create(any()))
                .thenReturn(
                        new Listing(
                                id,
                                tenantId,
                                categoryId,
                                brandId,
                                "RTX 5090",
                                new BigDecimal("1499.99"),
                                "USD",
                                "ACTIVE",
                                now,
                                now));

        String payload =
                """
                {
                  "tenantId": "%s",
                  "categoryId": "%s",
                  "brandId": "%s",
                  "title": "RTX 5090",
                  "basePrice": 1499.99,
                  "currency": "USD",
                  "status": "ACTIVE"
                }
                """
                        .formatted(tenantId, categoryId, brandId);

        mockMvc.perform(
                        post("/api/admin/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("RTX 5090"));
    }
}
