package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantUserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByTenantIdAndUsername(String tenantId, String username);

    boolean existsByTenantIdAndEmail(String tenantId, String email);

    Optional<UserEntity> findByTenantIdAndUsername(String tenantId, String username);

    Optional<UserEntity> findByIdAndTenantId(Long id, String tenantId);

    List<UserEntity> findAllByTenantId(String tenantId);
}
