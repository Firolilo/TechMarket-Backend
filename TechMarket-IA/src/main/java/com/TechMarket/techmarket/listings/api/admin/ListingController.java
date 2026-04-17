package com.techmarket.techmarket.listings.api.admin;

import com.techmarket.techmarket.listings.api.admin.request.CreateListingRequest;
import com.techmarket.techmarket.listings.api.admin.response.ListingResponse;
import com.techmarket.techmarket.listings.application.command.CreateListingCommand;
import com.techmarket.techmarket.listings.application.query.GetListingByIdQuery;
import com.techmarket.techmarket.listings.application.service.ListingApplicationService;
import com.techmarket.techmarket.listings.domain.model.Listing;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/listings")
public class ListingController {

    private final ListingApplicationService service;

    public ListingController(ListingApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse create(@Valid @RequestBody CreateListingRequest request) {
        Listing listing =
                service.create(
                        new CreateListingCommand(
                                request.tenantId(),
                                request.categoryId(),
                                request.brandId(),
                                request.title(),
                                request.basePrice(),
                                request.currency(),
                                request.status()));
        return toResponse(listing);
    }

    @GetMapping("/{id}")
    public ListingResponse getById(@PathVariable UUID id) {
        Listing listing = service.getById(new GetListingByIdQuery(id));
        if (listing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found");
        }
        return toResponse(listing);
    }

    private ListingResponse toResponse(Listing listing) {
        return new ListingResponse(
                listing.id(),
                listing.tenantId(),
                listing.categoryId(),
                listing.brandId(),
                listing.title(),
                listing.basePrice(),
                listing.currency(),
                listing.status(),
                listing.createdAt(),
                listing.updatedAt());
    }
}
