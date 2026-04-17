package com.techmarket.core.iam.application.port;

import com.techmarket.core.iam.domain.model.Resource;
import java.util.Optional;

public interface ResourceRepositoryPort {
    Optional<Resource> findById(Long id);
}
