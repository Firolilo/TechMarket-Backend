package com.techmarket.techmarket.listings.application.service;

import com.techmarket.techmarket.listings.application.command.CreateListingCommand;
import com.techmarket.techmarket.listings.application.query.GetListingByIdQuery;
import com.techmarket.techmarket.listings.domain.model.Listing;
import com.techmarket.techmarket.listings.domain.port.ListingRepositoryPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListingApplicationService {

    private final ListingRepositoryPort repository;

    public ListingApplicationService(ListingRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Listing create(CreateListingCommand command) {
        OffsetDateTime now = OffsetDateTime.now();
        Listing listing =
                new Listing(
                        UUID.randomUUID(),
                        command.tenantId(),
                        command.categoryId(),
                        command.brandId(),
                        command.title(),
                        command.basePrice(),
                        command.currency(),
                        command.status(),
                        now,
                        now);
        return repository.save(listing);
    }

    @Transactional(readOnly = true)
    public Listing getById(GetListingByIdQuery query) {
        return repository.findById(query.id()).orElse(null);
    }
}
