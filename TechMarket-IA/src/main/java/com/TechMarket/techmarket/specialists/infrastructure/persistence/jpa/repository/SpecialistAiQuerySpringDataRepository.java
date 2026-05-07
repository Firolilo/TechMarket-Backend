package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistAiQueryJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistAiQuerySpringDataRepository
        extends JpaRepository<SpecialistAiQueryJpaEntity, UUID> {}
