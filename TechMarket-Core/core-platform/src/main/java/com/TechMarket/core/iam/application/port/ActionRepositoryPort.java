package com.techmarket.core.iam.application.port;

import com.techmarket.core.iam.domain.model.Action;
import java.util.Optional;

public interface ActionRepositoryPort {
    Optional<Action> findById(Long id);
}
