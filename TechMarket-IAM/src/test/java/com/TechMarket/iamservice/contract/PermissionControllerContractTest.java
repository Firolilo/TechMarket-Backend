package com.techmarket.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techmarket.iamservice.application.dto.PermissionResponse;
import com.techmarket.iamservice.application.service.PermissionQueryService;
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
class PermissionControllerContractTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PermissionQueryService permissionQueryService;

    @Test
    void shouldReturn200WhenGetPermissions() throws Exception {
        PermissionResponse perm = new PermissionResponse(1L, 1L, 1L, 1L, 1L, null, "tenant-1");
        when(permissionQueryService.findAll()).thenReturn(List.of(perm));

        mockMvc.perform(get("/api/v1/iam/permissions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].roleId").value(1L))
                .andExpect(jsonPath("$[0].moduleId").value(1L));
    }

    @Test
    void shouldReturn200WhenGetPermissionsByTenant() throws Exception {
        PermissionResponse perm = new PermissionResponse(1L, 1L, 1L, 1L, 1L, null, "tenant-1");
        when(permissionQueryService.findAllByTenant(any())).thenReturn(List.of(perm));

        mockMvc.perform(
                        get("/api/v1/iam/permissions")
                                .header("X-Tenant-Id", "11111111-1111-1111-1111-111111111111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
