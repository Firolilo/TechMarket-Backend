package com.techmarket.techmarket.listings.domain.port;

import com.techmarket.techmarket.listings.domain.model.Listing;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepositoryPort {

    Listing save(Listing listing);

    Optional<Listing> findById(UUID id);
}
