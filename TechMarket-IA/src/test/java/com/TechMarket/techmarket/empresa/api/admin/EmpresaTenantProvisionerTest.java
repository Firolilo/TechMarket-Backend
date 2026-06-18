package com.techmarket.techmarket.empresa.api.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class EmpresaTenantProvisionerTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock private TenantSpringDataRepository tenantRepository;

    @Mock private UserSpringDataRepository userRepository;

    @Mock private JdbcTemplate jdbcTemplate;

    @InjectMocks private EmpresaTenantProvisioner provisioner;

    @Test
    void resolveOrCreate_shouldReturnExistingTenantWithoutCreating() {
        TenantJpaEntity existing = new TenantJpaEntity();
        existing.setId(UUID.randomUUID());
        existing.setBusinessName("Empresa existente");
        when(tenantRepository.findFirstByMemberUserId(USER_ID)).thenReturn(Optional.of(existing));

        TenantJpaEntity result = provisioner.resolveOrCreate(USER_ID);

        assertThat(result).isSameAs(existing);
        verify(tenantRepository, never()).save(any());
        verifyNoInteractions(userRepository, jdbcTemplate);
    }

    @Test
    void resolveOrCreate_shouldReturn404WhenUserDoesNotExist() {
        when(tenantRepository.findFirstByMemberUserId(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provisioner.resolveOrCreate(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex ->
                                assertThat(((ResponseStatusException) ex).getStatusCode())
                                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(tenantRepository, never()).save(any());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void resolveOrCreate_shouldCreateTenantAndOwnerMembershipUsingFullName() {
        when(tenantRepository.findFirstByMemberUserId(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user("Ana", "Quispe", "ana@x.com")));
        when(tenantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TenantJpaEntity result = provisioner.resolveOrCreate(USER_ID);

        ArgumentCaptor<TenantJpaEntity> tenantCaptor =
                ArgumentCaptor.forClass(TenantJpaEntity.class);
        verify(tenantRepository).save(tenantCaptor.capture());
        TenantJpaEntity saved = tenantCaptor.getValue();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBusinessName()).isEqualTo("Ana Quispe");
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(result).isSameAs(saved);

        // tenant_members insert: usuario queda como OWNER del tenant recien creado.
        ArgumentCaptor<Object> argsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(jdbcTemplate)
                .update(
                        eq(
                                "INSERT INTO tenant_members (id, tenant_id, user_id, tenant_role,"
                                        + " status, invited_by_user_id, joined_at) VALUES (?, ?, ?,"
                                        + " 'OWNER', 'ACTIVE', NULL, ?)"),
                        argsCaptor.capture(),
                        argsCaptor.capture(),
                        argsCaptor.capture(),
                        argsCaptor.capture());
        assertThat(argsCaptor.getAllValues()).contains(saved.getId(), USER_ID);
    }

    @Test
    void resolveOrCreate_shouldFallBackToEmailLocalPartWhenNamesBlank() {
        when(tenantRepository.findFirstByMemberUserId(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user("  ", "", "contacto@empresa.com")));
        when(tenantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        provisioner.resolveOrCreate(USER_ID);

        ArgumentCaptor<TenantJpaEntity> tenantCaptor =
                ArgumentCaptor.forClass(TenantJpaEntity.class);
        verify(tenantRepository).save(tenantCaptor.capture());
        assertThat(tenantCaptor.getValue().getBusinessName()).isEqualTo("contacto");
    }

    @Test
    void resolveOrCreate_shouldFallBackToDefaultNameWhenNamesBlankAndEmailInvalid() {
        when(tenantRepository.findFirstByMemberUserId(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(null, null, null)));
        when(tenantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        provisioner.resolveOrCreate(USER_ID);

        ArgumentCaptor<TenantJpaEntity> tenantCaptor =
                ArgumentCaptor.forClass(TenantJpaEntity.class);
        verify(tenantRepository).save(tenantCaptor.capture());
        assertThat(tenantCaptor.getValue().getBusinessName()).isEqualTo("Mi empresa");
    }

    private UserJpaEntity user(String firstName, String lastName, String email) {
        UserJpaEntity user = new UserJpaEntity();
        user.setId(USER_ID);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return user;
    }
}
