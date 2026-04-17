package com.techmarket.techmarket.users.api.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.techmarket.users.application.service.UserApplicationService;
import com.techmarket.techmarket.users.domain.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserApplicationService service;

    @Test
    void create_shouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        when(service.create(any()))
                .thenReturn(
                        new User(id, "Ada", "Lovelace", "ada@techmarket.com", "ACTIVE", now, now));

        String payload =
                objectMapper.writeValueAsString(
                        new java.util.LinkedHashMap<>() {
                            {
                                put("firstName", "Ada");
                                put("lastName", "Lovelace");
                                put("email", "ada@techmarket.com");
                                put("status", "ACTIVE");
                            }
                        });

        mockMvc.perform(
                        post("/api/admin/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("ada@techmarket.com"));
    }
}
