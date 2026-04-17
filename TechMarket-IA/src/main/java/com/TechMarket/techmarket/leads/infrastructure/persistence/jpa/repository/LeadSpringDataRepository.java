package com.techmarket.techmarket.leads.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.leads.infrastructure.persistence.jpa.entity.LeadJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadSpringDataRepository extends JpaRepository<LeadJpaEntity, UUID> {}
