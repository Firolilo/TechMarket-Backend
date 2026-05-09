package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
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
}
