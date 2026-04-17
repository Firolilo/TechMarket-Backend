package com.techmarket.core.iam.infrastructure.persistence.adapter;

import com.techmarket.core.iam.application.port.ResourceRepositoryPort;
import com.techmarket.core.iam.domain.model.Resource;
import com.techmarket.core.iam.infrastructure.persistence.mapper.ResourceJpaMapper;
import com.techmarket.core.iam.infrastructure.persistence.repository.ResourceJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ResourceRepositoryAdapter implements ResourceRepositoryPort {

    private final ResourceJpaRepository resourceJpaRepository;
    private final ResourceJpaMapper mapper;

    public ResourceRepositoryAdapter(
            ResourceJpaRepository resourceJpaRepository, ResourceJpaMapper mapper) {
        this.resourceJpaRepository = resourceJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceJpaRepository.findById(id).map(mapper::toDomain);
    }
}
