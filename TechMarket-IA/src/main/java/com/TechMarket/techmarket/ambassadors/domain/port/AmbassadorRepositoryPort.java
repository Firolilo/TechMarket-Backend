package com.techmarket.techmarket.ambassadors.domain.port;

import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorCommission;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorReferral;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AmbassadorRepositoryPort {

    Ambassador save(Ambassador ambassador);

    Optional<Ambassador> findByUserId(UUID userId);

    Optional<Ambassador> findById(UUID id);

    List<AmbassadorReferral> findReferralsByAmbassadorId(UUID ambassadorId);

    List<AmbassadorCommission> findCommissionsByAmbassadorId(UUID ambassadorId);
}
