package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorCommissionSpringDataRepository
        extends JpaRepository<AmbassadorCommissionJpaEntity, UUID> {

    List<AmbassadorCommissionJpaEntity> findByAmbassadorId(UUID ambassadorId);

    Optional<AmbassadorCommissionJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);

    default BigDecimal sumAmountByAmbassadorId(UUID ambassadorId) {
        return findByAmbassadorId(ambassadorId).stream()
                .map(AmbassadorCommissionJpaEntity::getAmount)
                .map(this::parseAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal parseAmount(String amount) {
        if (amount == null || amount.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(amount.replace("Bs", "").replace(",", "").trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}
