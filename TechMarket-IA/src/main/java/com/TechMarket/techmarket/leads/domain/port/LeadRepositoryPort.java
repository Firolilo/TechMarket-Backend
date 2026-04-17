package com.techmarket.techmarket.leads.domain.port;

import com.techmarket.techmarket.leads.domain.model.Lead;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepositoryPort {

    Lead save(Lead lead);

    Optional<Lead> findById(UUID id);
}
