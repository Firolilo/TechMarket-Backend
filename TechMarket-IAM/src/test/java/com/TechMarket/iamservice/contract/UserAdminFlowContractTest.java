package com.techmarket.iamservice.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import com.techmarket.iamservice.application.service.PrincipalAccessService;
import com.techmarket.iamservice.infrastructure.persistence.entity.BranchEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.BranchRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserAdminFlowContractTest {

    private static final String TENANT_ID = "66666666-6666-6666-6666-666666666666";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TenantRoleRepository tenantRoleRepository;
    @Autowired private BranchRepository branchRepository;
    @MockBean private PrincipalAccessService principalAccessService;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.when(
                        principalAccessService.isGlobalAdmin(org.mockito.ArgumentMatchers.any()))
                .thenReturn(true);
        org.mockito.Mockito.when(
                        principalAccessService.currentUserId(org.mockito.ArgumentMatchers.any()))
                .thenReturn(1L);
    }

    @Test
    void shouldCreateUserWithGlobalScope() throws Exception {
        RoleJpaEntity role = tenantRoleRepository.save(role("DOCENTE"));

        String requestBody =
                objectMapper.writeValueAsString(
                        new CreateUserRequestBody(
                                "docente.global",
                                "docente.global@example.com",
                                "StrongPass123",
                                null,
                                true,
                                java.util.Set.of(role.getId()),
                                "GLOBAL",
                                java.util.Set.of()));

        mockMvc.perform(
                        post("/users")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenantId").value(TENANT_ID))
                .andExpect(jsonPath("$.roleIds[0]").value(role.getId()))
                .andExpect(jsonPath("$.scopes[0].scopeType").value("GLOBAL"));
    }

    @Test
    void shouldRejectBranchScopeWithUnknownBranch() throws Exception {
        RoleJpaEntity role = tenantRoleRepository.save(role("AUXILIAR"));
        branchRepository.save(new BranchEntity(TENANT_ID, "SEDE_A", "Sede A", true));

        String requestBody =
                objectMapper.writeValueAsString(
                        new CreateUserRequestBody(
                                "docente.branch",
                                "docente.branch@example.com",
                                "StrongPass123",
                                null,
                                true,
                                java.util.Set.of(role.getId()),
                                "BRANCH",
                                java.util.Set.of(999999L)));

        mockMvc.perform(
                        post("/users")
                                .header("X-Tenant-Id", TENANT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    private RoleJpaEntity role(String name) {
        RoleJpaEntity role = new RoleJpaEntity(name, name + " role");
        role.setTenantId(TENANT_ID);
        return role;
    }

    private record CreateUserRequestBody(
            String username,
            String email,
            String password,
            String tenantId,
            Boolean active,
            java.util.Set<Long> roleIds,
            String scopeType,
            java.util.Set<Long> branchIds) {}
}
