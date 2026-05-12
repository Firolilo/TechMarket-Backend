package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistReviewJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecialistReviewSpringDataRepository
        extends JpaRepository<SpecialistReviewJpaEntity, UUID> {

    @Query(
            value =
                    "SELECT r.id AS id, u.first_name AS \"customerFirstName\", "
                            + "u.last_name AS \"customerLastName\", r.rating AS rating, "
                            + "r.comment AS comment, r.technician_response AS \"technicianResponse\", "
                            + "t.subject AS \"serviceName\", "
                            + "to_char(r.created_at AT TIME ZONE 'UTC', "
                            + "'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS \"createdAt\" "
                            + "FROM reviews r "
                            + "JOIN service_appointments sa ON sa.ticket_id = r.ticket_id "
                            + "LEFT JOIN users u ON u.id = r.user_id "
                            + "LEFT JOIN tickets t ON t.id = r.ticket_id "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "ORDER BY r.created_at DESC NULLS LAST",
            nativeQuery = true)
    List<SpecialistReviewProjection> findAllByTechnicianUserId(@Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT r.id AS id, u.first_name AS \"customerFirstName\", "
                            + "u.last_name AS \"customerLastName\", r.rating AS rating, "
                            + "r.comment AS comment, r.technician_response AS \"technicianResponse\", "
                            + "t.subject AS \"serviceName\", "
                            + "to_char(r.created_at AT TIME ZONE 'UTC', "
                            + "'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS \"createdAt\" "
                            + "FROM reviews r "
                            + "JOIN service_appointments sa ON sa.ticket_id = r.ticket_id "
                            + "LEFT JOIN users u ON u.id = r.user_id "
                            + "LEFT JOIN tickets t ON t.id = r.ticket_id "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "AND r.id = :reviewId",
            nativeQuery = true)
    Optional<SpecialistReviewProjection> findByIdAndTechnicianUserId(
            @Param("reviewId") UUID reviewId, @Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT r.* FROM reviews r "
                            + "JOIN service_appointments sa ON sa.ticket_id = r.ticket_id "
                            + "WHERE r.id = :reviewId "
                            + "AND sa.assigned_technician_user_id = :userId",
            nativeQuery = true)
    Optional<SpecialistReviewJpaEntity> findEntityByIdAndTechnicianUserId(
            @Param("reviewId") UUID reviewId, @Param("userId") UUID userId);
}
