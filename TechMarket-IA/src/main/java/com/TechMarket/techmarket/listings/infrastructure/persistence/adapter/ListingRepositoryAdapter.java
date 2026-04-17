package com.techmarket.techmarket.listings.infrastructure.persistence.adapter;

import com.techmarket.techmarket.listings.domain.model.Listing;
import com.techmarket.techmarket.listings.domain.port.ListingRepositoryPort;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.listings.infrastructure.persistence.mapper.ListingPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ListingRepositoryAdapter implements ListingRepositoryPort {

    private final ListingSpringDataRepository springDataRepository;
    private final ListingPersistenceMapper mapper;

    public ListingRepositoryAdapter(
            ListingSpringDataRepository springDataRepository, ListingPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Listing save(Listing listing) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(listing)));
    }

    @Override
    public Optional<Listing> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
}
