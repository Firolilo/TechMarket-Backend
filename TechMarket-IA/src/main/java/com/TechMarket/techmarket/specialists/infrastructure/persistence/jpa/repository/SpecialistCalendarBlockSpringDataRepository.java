package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistCalendarBlockJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistCalendarBlockSpringDataRepository
        extends JpaRepository<SpecialistCalendarBlockJpaEntity, UUID> {

    List<SpecialistCalendarBlockJpaEntity> findAllByUserIdOrderByBlockDateAscStartTimeAsc(
            UUID userId);

    Optional<SpecialistCalendarBlockJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
