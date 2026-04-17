package com.techmarket.core.iam.infrastructure.persistence.repository;

import com.techmarket.core.iam.infrastructure.persistence.entity.ActionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionJpaRepository
        extends JpaRepository<ActionJpaEntity, Long>, JpaSpecificationExecutor<ActionJpaEntity> {}
