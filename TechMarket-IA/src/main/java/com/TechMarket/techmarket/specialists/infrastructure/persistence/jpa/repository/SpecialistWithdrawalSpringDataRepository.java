package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistWithdrawalJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistWithdrawalSpringDataRepository
        extends JpaRepository<SpecialistWithdrawalJpaEntity, UUID> {}
