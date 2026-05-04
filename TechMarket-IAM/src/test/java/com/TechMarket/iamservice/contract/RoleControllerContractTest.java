package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.CreateRoleRequest;
import com.techmarket.iamservice.application.dto.RoleResponse;
import com.techmarket.iamservice.application.orchestration.CreateRoleOrchestrator;
import com.techmarket.iamservice.application.service.RoleManagementService;
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
class RoleControllerContractTest {

    private static final String TENANT_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private RoleManagementService roleManagementService;
    @MockBean private CreateRoleOrchestrator createRoleOrchestrator;

    @Test
    void shouldReturn200WhenGetRoles() throws Exception {
        RoleResponse role =
                new RoleResponse(1L, "ADMIN", "Administrator", "tenant-1", Set.of(), 100, null);
        when(roleManagementService.findAll()).thenReturn(List.of(role));

        mockMvc.perform(get("/api/v1/iam/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[0].description").value("Administrator"));
    }

    @Test
    void shouldReturn201WhenCreateRoleSuccessfully() throws Exception {
        RoleResponse role =
                new RoleResponse(1L, "ADMIN", "Administrator", "tenant-1", Set.of(), 100, null);
        when(createRoleOrchestrator.execute(anyString(), any(CreateRoleRequest.class), any()))
                .thenReturn(role);

        CreateRoleRequest request =
                new CreateRoleRequest("ADMIN", "Administrator", TENANT_ID, null, null);
        mockMvc.perform(
                        post("/api/v1/iam/roles")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.description").value("Administrator"));
    }

    @Test
    void shouldReturn400WhenCreateRoleWithEmptyName() throws Exception {
        String requestBody =
                """
                {"name": "", "description": "test"}
                """;

        mockMvc.perform(
                        post("/api/v1/iam/roles")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/iam/roles"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        when(createRoleOrchestrator.execute(anyString(), any(CreateRoleRequest.class), any()))
                .thenThrow(new RuntimeException("Unexpected"));

        CreateRoleRequest request =
                new CreateRoleRequest("ADMIN", "Administrator", TENANT_ID, null, null);
        mockMvc.perform(
                        post("/api/v1/iam/roles")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.IAM_INTERNAL_ERROR))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
