package com.techmarket.techmarket.reports.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.reports.infrastructure.persistence.jpa.entity.UserReportJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportSpringDataRepository extends JpaRepository<UserReportJpaEntity, UUID> {

    List<UserReportJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<UserReportJpaEntity> findAllByStatusOrderByCreatedAtDesc(String status);

    Optional<UserReportJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    long countByStatus(String status);
}
