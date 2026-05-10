package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateSpecialistPortfolioItemRequest;
import com.techmarket.techmarket.specialists.api.admin.response.CreateSpecialistPortfolioItemResponse;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistPortfolioItemResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistPortfolioItemJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistPortfolioItemSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/portfolio")
public class SpecialistPortfolioController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistPortfolioItemSpringDataRepository portfolioRepository;

    public SpecialistPortfolioController(
            SpecialistIdentitySupport identitySupport,
            SpecialistPortfolioItemSpringDataRepository portfolioRepository) {
        this.identitySupport = identitySupport;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping
    public List<SpecialistPortfolioItemResponse> portfolio(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return portfolioRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSpecialistPortfolioItemResponse createPortfolioItem(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSpecialistPortfolioItemRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        SpecialistPortfolioItemJpaEntity item = new SpecialistPortfolioItemJpaEntity();
        item.setId(UUID.randomUUID());
        item.setUserId(currentUserId);
        item.setTitle(request.titulo());
        item.setServiceName(request.servicio());
        item.setResult(request.resultado());
        item.setWorkDate(request.fecha());
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        SpecialistPortfolioItemJpaEntity saved = portfolioRepository.save(item);
        return new CreateSpecialistPortfolioItemResponse(
                identitySupport.formatPortfolioId(saved.getId()), "Trabajo agregado al portafolio");
    }

    @DeleteMapping("/{itemId}")
    public MessageResponse deletePortfolioItem(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String itemId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistPortfolioItemJpaEntity item =
                portfolioRepository
                        .findByIdAndUserId(parsePortfolioId(itemId), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Portfolio item not found"));
        portfolioRepository.delete(item);
        return new MessageResponse("Trabajo eliminado del portafolio");
    }

    private SpecialistPortfolioItemResponse toResponse(SpecialistPortfolioItemJpaEntity item) {
        return new SpecialistPortfolioItemResponse(
                identitySupport.formatPortfolioId(item.getId()),
                item.getTitle(),
                item.getServiceName(),
                item.getResult(),
                item.getWorkDate());
    }

    private UUID parsePortfolioId(String itemId) {
        return identitySupport.parsePrefixedUuid(itemId, "PORT-");
    }
}
