package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.marketplace.api.admin.response.CompanySummaryResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.ProductSummaryResponse;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.api.admin.client.response.CommunityPostResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CommunityResponse;
import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientFavoriteJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityMembershipJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityPostJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientFavoriteSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunityMembershipSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunityPostSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunitySpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients")
public class ClientFavoritesCommunityController {

    private final ClientFavoriteSpringDataRepository favoriteRepository;
    private final ListingSpringDataRepository listingRepository;
    private final TenantSpringDataRepository tenantRepository;
    private final CommunitySpringDataRepository communityRepository;
    private final CommunityMembershipSpringDataRepository membershipRepository;
    private final CommunityPostSpringDataRepository postRepository;
    private final UserSpringDataRepository userRepository;

    public ClientFavoritesCommunityController(
            ClientFavoriteSpringDataRepository favoriteRepository,
            ListingSpringDataRepository listingRepository,
            TenantSpringDataRepository tenantRepository,
            CommunitySpringDataRepository communityRepository,
            CommunityMembershipSpringDataRepository membershipRepository,
            CommunityPostSpringDataRepository postRepository,
            UserSpringDataRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.listingRepository = listingRepository;
        this.tenantRepository = tenantRepository;
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/favorites/products")
    public List<ProductSummaryResponse> favoriteProducts(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return favoriteRepository.findAllByUserIdAndListingIdIsNotNull(currentUserId).stream()
                .map(ClientFavoriteJpaEntity::getListingId)
                .map(listingRepository::findById)
                .flatMap(Optional::stream)
                .map(this::toProductSummary)
                .toList();
    }

    @PostMapping("/favorites/products/{productId}")
    public MessageResponse addFavoriteProduct(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String productId) {
        UUID currentUserId = parseUserId(userId);
        UUID listingId = parsePrefixedUuid(productId, "PROD-");
        ListingJpaEntity listing =
                listingRepository
                        .findById(listingId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Product not found"));
        favoriteRepository
                .findByUserIdAndListingId(currentUserId, listingId)
                .orElseGet(
                        () -> {
                            ClientFavoriteJpaEntity favorite = new ClientFavoriteJpaEntity();
                            favorite.setId(UUID.randomUUID());
                            favorite.setUserId(currentUserId);
                            favorite.setListingId(listingId);
                            favorite.setTenantId(listing.getTenantId());
                            favorite.setCreatedAt(OffsetDateTime.now());
                            return favoriteRepository.save(favorite);
                        });
        return new MessageResponse("Agregado a favoritos");
    }

    @DeleteMapping("/favorites/products/{productId}")
    public MessageResponse removeFavoriteProduct(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String productId) {
        UUID currentUserId = parseUserId(userId);
        UUID listingId = parsePrefixedUuid(productId, "PROD-");
        favoriteRepository
                .findByUserIdAndListingId(currentUserId, listingId)
                .ifPresent(favoriteRepository::delete);
        return new MessageResponse("Removido de favoritos");
    }

    @GetMapping("/favorites/companies")
    public List<CompanySummaryResponse> favoriteCompanies(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return favoriteRepository
                .findAllByUserIdAndTenantIdIsNotNullAndListingIdIsNull(currentUserId)
                .stream()
                .map(ClientFavoriteJpaEntity::getTenantId)
                .map(tenantRepository::findById)
                .flatMap(Optional::stream)
                .map(this::toCompanySummary)
                .toList();
    }

    @PostMapping("/favorites/companies/{companyId}")
    public MessageResponse followCompany(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String companyId) {
        UUID currentUserId = parseUserId(userId);
        UUID tenantId = parsePrefixedUuid(companyId, "EMP-");
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
        favoriteRepository
                .findByUserIdAndTenantIdAndListingIdIsNull(currentUserId, tenantId)
                .orElseGet(
                        () -> {
                            ClientFavoriteJpaEntity favorite = new ClientFavoriteJpaEntity();
                            favorite.setId(UUID.randomUUID());
                            favorite.setUserId(currentUserId);
                            favorite.setTenantId(tenantId);
                            favorite.setCreatedAt(OffsetDateTime.now());
                            return favoriteRepository.save(favorite);
                        });
        return new MessageResponse("Ahora sigues a esta empresa");
    }

    @DeleteMapping("/favorites/companies/{companyId}")
    public MessageResponse unfollowCompany(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String companyId) {
        UUID currentUserId = parseUserId(userId);
        UUID tenantId = parsePrefixedUuid(companyId, "EMP-");
        favoriteRepository
                .findByUserIdAndTenantIdAndListingIdIsNull(currentUserId, tenantId)
                .ifPresent(favoriteRepository::delete);
        return new MessageResponse("Dejaste de seguir a esta empresa");
    }

    @GetMapping("/communities")
    public List<CommunityResponse> communities(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return membershipRepository.findAllByUserId(currentUserId).stream()
                .map(CommunityMembershipJpaEntity::getCommunityId)
                .map(communityRepository::findById)
                .flatMap(Optional::stream)
                .map(this::toCommunityResponse)
                .toList();
    }

    @PostMapping("/communities/{communityId}/join")
    @Transactional
    public MessageResponse joinCommunity(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        membershipRepository
                .findByCommunityIdAndUserId(community.getId(), currentUserId)
                .orElseGet(
                        () -> {
                            CommunityMembershipJpaEntity membership =
                                    new CommunityMembershipJpaEntity();
                            membership.setId(UUID.randomUUID());
                            membership.setCommunityId(community.getId());
                            membership.setUserId(currentUserId);
                            membership.setJoinedAt(OffsetDateTime.now());
                            community.setMembersCount(community.getMembersCount() + 1);
                            communityRepository.save(community);
                            return membershipRepository.save(membership);
                        });
        return new MessageResponse("Te has unido a la comunidad");
    }

    @DeleteMapping("/communities/{communityId}/leave")
    @Transactional
    public MessageResponse leaveCommunity(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        membershipRepository
                .findByCommunityIdAndUserId(community.getId(), currentUserId)
                .ifPresent(
                        membership -> {
                            membershipRepository.delete(membership);
                            community.setMembersCount(Math.max(0, community.getMembersCount() - 1));
                            communityRepository.save(community);
                        });
        return new MessageResponse("Has salido de la comunidad");
    }

    @GetMapping("/communities/{communityId}/posts")
    public List<CommunityPostResponse> communityPosts(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId) {
        parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        return postRepository.findAllByCommunityIdOrderByCreatedAtDesc(community.getId()).stream()
                .map(this::toCommunityPostResponse)
                .toList();
    }

    private ProductSummaryResponse toProductSummary(ListingJpaEntity listing) {
        return new ProductSummaryResponse(
                formatProductId(listing.getId()),
                listing.getTitle(),
                listing.getBasePrice(),
                null,
                0);
    }

    private CompanySummaryResponse toCompanySummary(TenantJpaEntity tenant) {
        return new CompanySummaryResponse(
                formatCompanyId(tenant.getId()), tenant.getBusinessName(), null, 0);
    }

    private CommunityResponse toCommunityResponse(CommunityJpaEntity community) {
        return new CommunityResponse(
                formatCommunityId(community.getId()),
                community.getName(),
                community.getMembersCount());
    }

    private CommunityPostResponse toCommunityPostResponse(CommunityPostJpaEntity post) {
        String author =
                post.getAuthorUserId() == null
                        ? "Usuario"
                        : userRepository
                                .findById(post.getAuthorUserId())
                                .map(user -> displayName(user.getFirstName(), user.getLastName()))
                                .orElse("Usuario");
        return new CommunityPostResponse(formatPostId(post.getId()), author, post.getContent());
    }

    private String displayName(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        String fullName = (first + " " + last).trim();
        return fullName.isBlank() ? "Usuario" : fullName;
    }

    private CommunityJpaEntity findCommunity(String communityId) {
        return communityRepository
                .findById(parsePrefixedUuid(communityId, "COM-"))
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Community not found"));
    }

    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id is required");
        }
        try {
            return UUID.fromString(userId.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id is invalid");
        }
    }

    private UUID parsePrefixedUuid(String value, String prefix) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.regionMatches(true, 0, prefix, 0, prefix.length())) {
            normalized = normalized.substring(prefix.length());
        }
        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier is invalid");
        }
    }

    private String formatProductId(UUID id) {
        return "PROD-" + id;
    }

    private String formatCompanyId(UUID id) {
        return "EMP-" + id;
    }

    private String formatCommunityId(UUID id) {
        return "COM-" + id;
    }

    private String formatPostId(UUID id) {
        return "POST-" + id;
    }
}
