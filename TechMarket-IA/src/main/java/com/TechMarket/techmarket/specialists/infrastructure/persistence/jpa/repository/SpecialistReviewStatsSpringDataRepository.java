package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistReviewJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecialistReviewStatsSpringDataRepository
        extends JpaRepository<SpecialistReviewJpaEntity, UUID> {

    @Query(
            value =
                    "SELECT COUNT(r.id) AS \"totalReviews\", "
                            + "COALESCE(AVG(r.rating), 0) AS \"averageRating\" "
                            + "FROM reviews r "
                            + "WHERE r.ticket_id IN ("
                            + "SELECT sa.ticket_id FROM service_appointments sa "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "AND sa.ticket_id IS NOT NULL)",
            nativeQuery = true)
    SpecialistReviewStatsProjection findStatsByTechnicianUserId(@Param("userId") UUID userId);
}
