package com.techmarket.core.iam.infrastructure.persistence.adapter;

import com.techmarket.core.iam.application.port.RoleRepositoryPort;
import com.techmarket.core.iam.domain.model.Role;
import com.techmarket.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import com.techmarket.core.iam.infrastructure.persistence.mapper.RoleJpaMapper;
import com.techmarket.core.iam.infrastructure.persistence.repository.PermissionJpaRepository;
import com.techmarket.core.iam.infrastructure.persistence.repository.RoleJpaRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    private final RoleJpaMapper mapper;

    public RoleRepositoryAdapter(
            RoleJpaRepository roleJpaRepository,
            PermissionJpaRepository permissionJpaRepository,
            RoleJpaMapper mapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RoleJpaEntity entity;
        if (role.getId() == null) {
            entity = new RoleJpaEntity(role.getName(), role.getDescription());
            entity.setTenantId(role.getTenantId());
            entity = roleJpaRepository.save(entity);
        } else {
            entity =
                    roleJpaRepository
                            .findById(role.getId())
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Role not found for update: " + role.getId()));
            entity.setName(role.getName());
            entity.setDescription(role.getDescription());
            roleJpaRepository.save(entity);
        }

        Set<Long> targetPermissionIds =
                role.getPermissionIds() == null ? Set.of() : role.getPermissionIds();
        synchronizeRolePermissions(entity, targetPermissionIds);

        var saved = roleJpaRepository.findById(entity.getId()).orElse(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Set<Role> findByIds(Set<Long> ids) {
        return roleJpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toSet());
    }

    private void synchronizeRolePermissions(RoleJpaEntity role, Set<Long> targetPermissionIds) {
        List<PermissionJpaEntity> currentPermissions =
                permissionJpaRepository.findByRoleId(role.getId()).stream().toList();
        Set<Long> targetIds = new HashSet<>(targetPermissionIds);

        for (PermissionJpaEntity currentPermission : currentPermissions) {
            if (!targetIds.contains(currentPermission.getId())) {
                permissionJpaRepository.delete(currentPermission);
            }
        }

        if (targetIds.isEmpty()) {
            return;
        }

        List<PermissionJpaEntity> targetPermissions =
                permissionJpaRepository.findAllById(targetIds);
        for (PermissionJpaEntity permission : targetPermissions) {
            permission.setRole(role);
        }
        permissionJpaRepository.saveAll(targetPermissions);
    }
}
