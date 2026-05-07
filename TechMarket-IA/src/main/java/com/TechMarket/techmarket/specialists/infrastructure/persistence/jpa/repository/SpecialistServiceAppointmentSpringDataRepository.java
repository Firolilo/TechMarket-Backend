package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecialistServiceAppointmentSpringDataRepository
        extends JpaRepository<SpecialistServiceAppointmentJpaEntity, UUID> {

    @Query(
            value =
                    "SELECT COUNT(*) FROM service_appointments "
                            + "WHERE assigned_technician_user_id = :userId "
                            + "AND LOWER(COALESCE(status, '')) IN "
                            + "('completed', 'completado', 'completada', "
                            + "'finalizado', 'finalizada')",
            nativeQuery = true)
    long countCompletedByTechnicianUserId(@Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT sa.id AS id, sa.ticket_id AS ticketId, "
                            + "u.first_name AS customerFirstName, u.last_name AS customerLastName, "
                            + "u.phone AS customerPhone, t.subject AS serviceName, "
                            + "t.description AS description, t.priority AS priority, "
                            + "sa.status AS status, sa.start_at AS startAt, "
                            + "sa.location AS location, sa.notes AS notes "
                            + "FROM service_appointments sa "
                            + "LEFT JOIN tickets t ON t.id = sa.ticket_id "
                            + "LEFT JOIN users u ON u.id = t.customer_user_id "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "ORDER BY sa.start_at ASC NULLS LAST",
            nativeQuery = true)
    List<SpecialistAppointmentSummaryProjection> findCalendarByTechnicianUserId(
            @Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT sa.id AS id, sa.ticket_id AS ticketId, "
                            + "u.first_name AS customerFirstName, u.last_name AS customerLastName, "
                            + "u.phone AS customerPhone, t.subject AS serviceName, "
                            + "t.description AS description, t.priority AS priority, "
                            + "sa.status AS status, sa.start_at AS startAt, "
                            + "sa.location AS location, sa.notes AS notes "
                            + "FROM service_appointments sa "
                            + "LEFT JOIN tickets t ON t.id = sa.ticket_id "
                            + "LEFT JOIN users u ON u.id = t.customer_user_id "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "AND LOWER(COALESCE(sa.status, t.status, '')) IN "
                            + "('pending', 'pendiente', 'requested', 'solicitado', 'solicitada') "
                            + "ORDER BY sa.start_at ASC NULLS LAST",
            nativeQuery = true)
    List<SpecialistAppointmentSummaryProjection> findRequestsByTechnicianUserId(
            @Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT sa.id AS id, sa.ticket_id AS ticketId, "
                            + "u.first_name AS customerFirstName, u.last_name AS customerLastName, "
                            + "u.phone AS customerPhone, t.subject AS serviceName, "
                            + "t.description AS description, t.priority AS priority, "
                            + "sa.status AS status, sa.start_at AS startAt, "
                            + "sa.location AS location, sa.notes AS notes "
                            + "FROM service_appointments sa "
                            + "LEFT JOIN tickets t ON t.id = sa.ticket_id "
                            + "LEFT JOIN users u ON u.id = t.customer_user_id "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "AND LOWER(COALESCE(sa.status, '')) IN "
                            + "('accepted', 'aceptado', 'aceptada', 'confirmed', 'confirmada', "
                            + "'in_progress', 'en_progreso', 'asignado', 'asignada') "
                            + "ORDER BY sa.start_at ASC NULLS LAST",
            nativeQuery = true)
    List<SpecialistAppointmentSummaryProjection> findActiveProjectsByTechnicianUserId(
            @Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT sa.id AS id, sa.ticket_id AS ticketId, "
                            + "u.first_name AS customerFirstName, u.last_name AS customerLastName, "
                            + "u.phone AS customerPhone, t.subject AS serviceName, "
                            + "t.description AS description, t.priority AS priority, "
                            + "sa.status AS status, sa.start_at AS startAt, "
                            + "sa.location AS location, sa.notes AS notes "
                            + "FROM service_appointments sa "
                            + "LEFT JOIN tickets t ON t.id = sa.ticket_id "
                            + "LEFT JOIN users u ON u.id = t.customer_user_id "
                            + "WHERE sa.assigned_technician_user_id = :userId "
                            + "AND LOWER(COALESCE(sa.status, '')) IN "
                            + "('completed', 'completado', 'completada', 'finalizado', "
                            + "'finalizada', 'cancelled', 'cancelado', 'cancelada') "
                            + "ORDER BY sa.start_at DESC NULLS LAST",
            nativeQuery = true)
    List<SpecialistAppointmentSummaryProjection> findProjectHistoryByTechnicianUserId(
            @Param("userId") UUID userId);

    @Query(
            value =
                    "SELECT sa.id AS id, sa.ticket_id AS ticketId, "
                            + "u.first_name AS customerFirstName, u.last_name AS customerLastName, "
                            + "u.phone AS customerPhone, t.subject AS serviceName, "
                            + "t.description AS description, t.priority AS priority, "
                            + "sa.status AS status, sa.start_at AS startAt, "
                            + "sa.location AS location, sa.notes AS notes "
                            + "FROM service_appointments sa "
                            + "LEFT JOIN tickets t ON t.id = sa.ticket_id "
                            + "LEFT JOIN users u ON u.id = t.customer_user_id "
                            + "WHERE sa.id = :appointmentId "
                            + "AND sa.assigned_technician_user_id = :userId",
            nativeQuery = true)
    Optional<SpecialistAppointmentSummaryProjection> findSummaryByIdAndTechnicianUserId(
            @Param("appointmentId") UUID appointmentId, @Param("userId") UUID userId);

    Optional<SpecialistServiceAppointmentJpaEntity> findByIdAndAssignedTechnicianUserId(
            UUID id, UUID assignedTechnicianUserId);
}
