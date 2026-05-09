package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistTransactionJpaEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecialistTransactionSpringDataRepository
        extends JpaRepository<SpecialistTransactionJpaEntity, UUID> {

    List<SpecialistTransactionJpaEntity> findAllByUserIdOrderByTransactionDateDesc(UUID userId);

    Optional<SpecialistTransactionJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query(
            "select coalesce(sum(t.amount - t.platformCommission), 0) "
                    + "from SpecialistTransactionJpaEntity t "
                    + "where t.userId = :userId "
                    + "and lower(t.status) in ('completado', 'completed', 'disponible', 'available')")
    BigDecimal sumAvailableByUserId(@Param("userId") UUID userId);

    @Query(
            "select coalesce(sum(t.amount), 0) "
                    + "from SpecialistTransactionJpaEntity t "
                    + "where t.userId = :userId "
                    + "and lower(t.status) in ('completado', 'completed', 'disponible', 'available')")
    BigDecimal sumCompletedGrossByUserId(@Param("userId") UUID userId);

    @Query(
            "select coalesce(sum(t.amount - t.platformCommission), 0) "
                    + "from SpecialistTransactionJpaEntity t "
                    + "where t.userId = :userId "
                    + "and lower(t.status) in ('pendiente', 'pending', 'en_proceso', 'processing')")
    BigDecimal sumInProcessByUserId(@Param("userId") UUID userId);

    @Query(
            "select count(t) from SpecialistTransactionJpaEntity t "
                    + "where t.userId = :userId "
                    + "and lower(t.status) in ('completado', 'completed', 'disponible', 'available')")
    long countCompletedByUserId(@Param("userId") UUID userId);
}
