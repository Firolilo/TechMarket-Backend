package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.BranchResponse;
import com.techmarket.iamservice.application.dto.CreateBranchRequest;
import com.techmarket.iamservice.application.service.BranchManagementService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BranchControllerContractTest {

    private static final String TENANT_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private BranchManagementService branchManagementService;

    @Test
    void shouldReturn200WhenGetBranches() throws Exception {
        BranchResponse branch = new BranchResponse(1L, TENANT_ID, "SEDE_A", "Sede A", true);
        when(branchManagementService.findAllByTenant(any())).thenReturn(List.of(branch));

        mockMvc.perform(get("/api/v1/iam/branches").header("X-Tenant-Id", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].code").value("SEDE_A"))
                .andExpect(jsonPath("$[0].name").value("Sede A"));
    }

    @Test
    void shouldReturn400WhenGetBranchesWithoutTenant() throws Exception {
        mockMvc.perform(get("/api/v1/iam/branches"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.IAM_TENANT_REQUIRED));
    }

    @Test
    void shouldReturn201WhenCreateBranch() throws Exception {
        BranchResponse branch = new BranchResponse(1L, TENANT_ID, "SEDE_B", "Sede B", true);
        when(branchManagementService.create(anyString(), any(CreateBranchRequest.class), any()))
                .thenReturn(branch);

        CreateBranchRequest request = new CreateBranchRequest("SEDE_B", "Sede B", TENANT_ID, true);
        mockMvc.perform(
                        post("/api/v1/iam/branches")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.code").value("SEDE_B"));
    }

    @Test
    void shouldReturn400WhenCreateBranchWithBlankCode() throws Exception {
        String requestBody = """
                {"code": "", "name": "Sede B"}
                """;

        mockMvc.perform(
                        post("/api/v1/iam/branches")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.details").exists());
    }
}
