package com.techmarket.core.iam.application.port;

import com.techmarket.core.iam.domain.model.Module;
import java.util.Optional;

public interface ModuleRepositoryPort {
    Optional<Module> findById(Long id);
}
