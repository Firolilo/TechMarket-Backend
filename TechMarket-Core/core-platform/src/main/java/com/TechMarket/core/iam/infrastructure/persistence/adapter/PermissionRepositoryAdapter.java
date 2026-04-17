package com.techmarket.core.iam.infrastructure.persistence.adapter;

import com.techmarket.core.iam.application.port.PermissionRepositoryPort;
import com.techmarket.core.iam.domain.model.Permission;
import com.techmarket.core.iam.infrastructure.persistence.mapper.PermissionJpaMapper;
import com.techmarket.core.iam.infrastructure.persistence.repository.PermissionJpaRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PermissionJpaMapper mapper;

    public PermissionRepositoryAdapter(
            PermissionJpaRepository permissionJpaRepository, PermissionJpaMapper mapper) {
        this.permissionJpaRepository = permissionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Set<Permission> saveAll(Set<Permission> permissions) {
        throw new UnsupportedOperationException(
                "saveAll is not supported yet for PermissionRepositoryAdapter");
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        permissionJpaRepository.deleteByRoleId(roleId);
    }

    @Override
    public Set<Permission> findByIds(Set<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toSet());
    }
}
