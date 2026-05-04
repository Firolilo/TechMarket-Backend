package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.core.shared.exceptions.EntityNotFoundException;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.CreateUserRequest;
import com.techmarket.iamservice.application.dto.UpdateUserRequest;
import com.techmarket.iamservice.application.dto.UserResponse;
import com.techmarket.iamservice.application.service.UserManagementService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerContractTest {

    private static final String TENANT_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserManagementService userManagementService;

    @Test
    void shouldReturn200WhenGetUsers() throws Exception {
        UserResponse user =
                new UserResponse(
                        1L, "admin", "admin@test.com", true, TENANT_ID, Set.of(1L), Set.of());
        when(userManagementService.findAll(any())).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/iam/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].email").value("admin@test.com"));
    }

    @Test
    void shouldReturn200WhenGetUserById() throws Exception {
        UserResponse user =
                new UserResponse(
                        1L, "admin", "admin@test.com", true, TENANT_ID, Set.of(1L), Set.of());
        when(userManagementService.findById(anyLong(), any())).thenReturn(user);

        mockMvc.perform(get("/api/v1/iam/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userManagementService.findById(anyLong(), any()))
                .thenThrow(new EntityNotFoundException("User", "999"));

        mockMvc.perform(get("/api/v1/iam/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("error.entity.not_found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn201WhenCreateUserSuccessfully() throws Exception {
        UserResponse user =
                new UserResponse(
                        1L, "newuser", "new@test.com", true, TENANT_ID, Set.of(1L), Set.of());
        when(userManagementService.create(anyString(), any(CreateUserRequest.class), any()))
                .thenReturn(user);

        CreateUserRequest request =
                new CreateUserRequest(
                        "newuser",
                        "new@test.com",
                        "StrongPass123",
                        TENANT_ID,
                        true,
                        Set.of(1L),
                        "GLOBAL",
                        Set.of());

        mockMvc.perform(
                        post("/api/v1/iam/users")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void shouldReturn400WhenCreateUserWithInvalidEmail() throws Exception {
        String requestBody =
                """
                {
                  "username": "testuser",
                  "email": "invalid-email",
                  "password": "StrongPass123",
                  "roleIds": [1],
                  "scopeType": "GLOBAL"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/iam/users")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void shouldReturn200WhenUpdateUser() throws Exception {
        UserResponse user =
                new UserResponse(
                        1L, "admin", "updated@test.com", true, TENANT_ID, Set.of(1L), Set.of());
        when(userManagementService.update(anyLong(), any(UpdateUserRequest.class), any()))
                .thenReturn(user);

        UpdateUserRequest request =
                new UpdateUserRequest("updated@test.com", true, Set.of(1L), "GLOBAL", Set.of());

        mockMvc.perform(
                        put("/api/v1/iam/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }
}
