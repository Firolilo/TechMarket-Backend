package com.techmarket.core.iam.application.port;

import com.techmarket.core.iam.domain.model.Field;
import java.util.Optional;

public interface FieldRepositoryPort {
    Optional<Field> findById(Long id);
}
