package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.iamservice.infrastructure.persistence.entity.BranchEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<BranchEntity, Long> {

    List<BranchEntity> findAllByTenantId(String tenantId);

    Optional<BranchEntity> findByIdAndTenantId(Long id, String tenantId);

    Set<BranchEntity> findByIdInAndTenantId(Set<Long> ids, String tenantId);

    boolean existsByTenantIdAndCode(String tenantId, String code);
}
