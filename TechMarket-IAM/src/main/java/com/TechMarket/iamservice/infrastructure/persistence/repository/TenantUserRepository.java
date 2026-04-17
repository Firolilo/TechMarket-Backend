package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.core.iam.infrastructure.persistence.entity.UserJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantUserRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByTenantIdAndUsername(String tenantId, String username);

    boolean existsByTenantIdAndEmail(String tenantId, String email);

    Optional<UserJpaEntity> findByTenantIdAndUsername(String tenantId, String username);

    Optional<UserJpaEntity> findByIdAndTenantId(Long id, String tenantId);

    List<UserJpaEntity> findAllByTenantId(String tenantId);
}
