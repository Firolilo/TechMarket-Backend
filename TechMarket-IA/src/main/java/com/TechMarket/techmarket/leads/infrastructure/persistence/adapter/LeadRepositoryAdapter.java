package com.techmarket.techmarket.leads.infrastructure.persistence.adapter;

import com.techmarket.techmarket.leads.domain.model.Lead;
import com.techmarket.techmarket.leads.domain.port.LeadRepositoryPort;
import com.techmarket.techmarket.leads.infrastructure.persistence.jpa.repository.LeadSpringDataRepository;
import com.techmarket.techmarket.leads.infrastructure.persistence.mapper.LeadPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class LeadRepositoryAdapter implements LeadRepositoryPort {

    private final LeadSpringDataRepository springDataRepository;
    private final LeadPersistenceMapper mapper;

    public LeadRepositoryAdapter(
            LeadSpringDataRepository springDataRepository, LeadPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Lead save(Lead lead) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(lead)));
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
}
