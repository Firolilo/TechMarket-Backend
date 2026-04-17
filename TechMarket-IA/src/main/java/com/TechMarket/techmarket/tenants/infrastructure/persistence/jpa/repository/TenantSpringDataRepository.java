package com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantSpringDataRepository extends JpaRepository<TenantJpaEntity, UUID> {}
