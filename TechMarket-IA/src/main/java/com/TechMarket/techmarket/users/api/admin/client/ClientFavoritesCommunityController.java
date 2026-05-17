package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.marketplace.api.admin.response.CompanySummaryResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.ProductSummaryResponse;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.api.admin.client.response.CommunityMemberResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CommunityPostCommentResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CommunityPostResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CommunityResponse;
import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientFavoriteJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityMemberRoleJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityMembershipJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityPostJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityPostLikeJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.PostCommentJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientFavoriteSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunityMemberRoleSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunityMembershipSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunityPostLikeSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunityPostSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.CommunitySpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.PostCommentSpringDataRepository;
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
import org.springframework.web.bind.annotation.RequestBody;
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
    private final CommunityMemberRoleSpringDataRepository memberRoleRepository;
    private final CommunityPostLikeSpringDataRepository postLikeRepository;
    private final PostCommentSpringDataRepository postCommentRepository;

    public ClientFavoritesCommunityController(
            ClientFavoriteSpringDataRepository favoriteRepository,
            ListingSpringDataRepository listingRepository,
            TenantSpringDataRepository tenantRepository,
            CommunitySpringDataRepository communityRepository,
            CommunityMembershipSpringDataRepository membershipRepository,
            CommunityPostSpringDataRepository postRepository,
            UserSpringDataRepository userRepository,
            CommunityMemberRoleSpringDataRepository memberRoleRepository,
            CommunityPostLikeSpringDataRepository postLikeRepository,
            PostCommentSpringDataRepository postCommentRepository) {
        this.favoriteRepository = favoriteRepository;
        this.listingRepository = listingRepository;
        this.tenantRepository = tenantRepository;
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.memberRoleRepository = memberRoleRepository;
        this.postLikeRepository = postLikeRepository;
        this.postCommentRepository = postCommentRepository;
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
                .map(community -> toCommunityResponse(community, true))
                .toList();
    }

    @GetMapping("/communities/discover")
    public List<CommunityResponse> discoverCommunities(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        java.util.Set<UUID> joinedIds =
                membershipRepository.findAllByUserId(currentUserId).stream()
                        .map(CommunityMembershipJpaEntity::getCommunityId)
                        .collect(java.util.stream.Collectors.toSet());
        return communityRepository.findAll().stream()
                .map(
                        community ->
                                toCommunityResponse(
                                        community, joinedIds.contains(community.getId())))
                .toList();
    }

    @GetMapping("/communities/{communityId}")
    public CommunityResponse communityDetail(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        boolean joined =
                membershipRepository
                        .findByCommunityIdAndUserId(community.getId(), currentUserId)
                        .isPresent();
        return toCommunityResponse(community, joined);
    }

    @GetMapping("/communities/{communityId}/members")
    public List<CommunityMemberResponse> communityMembers(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        List<CommunityMembershipJpaEntity> memberships =
                membershipRepository.findAllByCommunityIdOrderByJoinedAtAsc(community.getId());
        return memberships.stream()
                .map(m -> toCommunityMemberResponse(m, currentUserId, resolveRole(m, memberships)))
                .toList();
    }

    @PostMapping("/communities/{communityId}/members/{memberUserId}/promote")
    @Transactional
    public MessageResponse promoteMember(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String memberUserId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID targetUserId = parsePrefixedUuid(memberUserId, "USR-");

        List<CommunityMembershipJpaEntity> memberships =
                membershipRepository.findAllByCommunityIdOrderByJoinedAtAsc(community.getId());
        String currentRole = roleForUser(currentUserId, memberships);

        if (!CommunityMemberRoleJpaEntity.ROLE_ADMIN.equals(currentRole)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo el administrador puede promover miembros");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No podés cambiar tu propio rol");
        }

        boolean targetIsMember =
                memberships.stream().anyMatch(m -> targetUserId.equals(m.getUserId()));
        if (!targetIsMember) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El usuario no es miembro de esta comunidad");
        }

        setMemberRole(community.getId(), targetUserId, CommunityMemberRoleJpaEntity.ROLE_MODERATOR);
        return new MessageResponse("Miembro promovido a moderador");
    }

    @PostMapping("/communities/{communityId}/members/{memberUserId}/demote")
    @Transactional
    public MessageResponse demoteMember(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String memberUserId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID targetUserId = parsePrefixedUuid(memberUserId, "USR-");

        List<CommunityMembershipJpaEntity> memberships =
                membershipRepository.findAllByCommunityIdOrderByJoinedAtAsc(community.getId());
        String currentRole = roleForUser(currentUserId, memberships);

        if (!CommunityMemberRoleJpaEntity.ROLE_ADMIN.equals(currentRole)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo el administrador puede degradar miembros");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No podés cambiar tu propio rol");
        }

        memberRoleRepository
                .findByCommunityIdAndUserId(community.getId(), targetUserId)
                .ifPresent(memberRoleRepository::delete);
        return new MessageResponse("Moderador degradado a miembro");
    }

    @DeleteMapping("/communities/{communityId}/members/{memberUserId}")
    @Transactional
    public MessageResponse kickMember(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String memberUserId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID targetUserId = parsePrefixedUuid(memberUserId, "USR-");

        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Usá 'salir de la comunidad' para irte vos mismo");
        }

        List<CommunityMembershipJpaEntity> memberships =
                membershipRepository.findAllByCommunityIdOrderByJoinedAtAsc(community.getId());
        String currentRole = roleForUser(currentUserId, memberships);
        String targetRole = roleForUser(targetUserId, memberships);

        boolean isAdmin = CommunityMemberRoleJpaEntity.ROLE_ADMIN.equals(currentRole);
        boolean isModerator = CommunityMemberRoleJpaEntity.ROLE_MODERATOR.equals(currentRole);

        if (!isAdmin && !isModerator) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permisos para expulsar miembros");
        }

        // Moderadores no pueden kickear a admins ni a otros moderadores.
        if (isModerator
                && (CommunityMemberRoleJpaEntity.ROLE_ADMIN.equals(targetRole)
                        || CommunityMemberRoleJpaEntity.ROLE_MODERATOR.equals(targetRole))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No podés expulsar a administradores ni a otros moderadores");
        }

        // El admin no puede ser expulsado nunca (siempre debe haber 1 admin).
        if (CommunityMemberRoleJpaEntity.ROLE_ADMIN.equals(targetRole)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El administrador no puede ser expulsado");
        }

        CommunityMembershipJpaEntity targetMembership =
                memberships.stream()
                        .filter(m -> targetUserId.equals(m.getUserId()))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "El usuario no es miembro de esta comunidad"));

        membershipRepository.delete(targetMembership);
        memberRoleRepository
                .findByCommunityIdAndUserId(community.getId(), targetUserId)
                .ifPresent(memberRoleRepository::delete);
        community.setMembersCount(Math.max(0, community.getMembersCount() - 1));
        communityRepository.save(community);

        return new MessageResponse("Miembro expulsado de la comunidad");
    }

    @PostMapping("/communities/{communityId}/posts")
    @Transactional
    public CommunityPostResponse createCommunityPost(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @RequestBody CreateCommunityPostRequest request) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);

        if (request == null
                || request.contenido() == null
                || request.contenido().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El contenido del post no puede estar vacio");
        }

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

        CommunityPostJpaEntity post = new CommunityPostJpaEntity();
        post.setId(UUID.randomUUID());
        post.setCommunityId(community.getId());
        post.setAuthorUserId(currentUserId);
        post.setTitle(request.titulo() == null ? null : request.titulo().trim());
        post.setContent(request.contenido().trim());
        post.setStatus("PUBLISHED");
        post.setCreatedAt(OffsetDateTime.now());
        postRepository.save(post);

        return toCommunityPostResponse(post, currentUserId);
    }

    public record CreateCommunityPostRequest(String titulo, String contenido) {}

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
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        return postRepository.findAllByCommunityIdOrderByCreatedAtDesc(community.getId()).stream()
                .map(post -> toCommunityPostResponse(post, currentUserId))
                .toList();
    }

    @PostMapping("/communities/{communityId}/posts/{postId}/like")
    @Transactional
    public CommunityPostResponse likePost(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String postId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID postUuid = parsePrefixedUuid(postId, "POST-");
        CommunityPostJpaEntity post =
                postRepository
                        .findById(postUuid)
                        .filter(p -> community.getId().equals(p.getCommunityId()))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Post no encontrado"));

        postLikeRepository
                .findByPostIdAndUserId(post.getId(), currentUserId)
                .orElseGet(
                        () -> {
                            CommunityPostLikeJpaEntity like = new CommunityPostLikeJpaEntity();
                            like.setId(UUID.randomUUID());
                            like.setPostId(post.getId());
                            like.setUserId(currentUserId);
                            like.setCreatedAt(OffsetDateTime.now());
                            return postLikeRepository.save(like);
                        });

        return toCommunityPostResponse(post, currentUserId);
    }

    @DeleteMapping("/communities/{communityId}/posts/{postId}/like")
    @Transactional
    public CommunityPostResponse unlikePost(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String postId) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID postUuid = parsePrefixedUuid(postId, "POST-");
        CommunityPostJpaEntity post =
                postRepository
                        .findById(postUuid)
                        .filter(p -> community.getId().equals(p.getCommunityId()))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Post no encontrado"));

        postLikeRepository
                .findByPostIdAndUserId(post.getId(), currentUserId)
                .ifPresent(postLikeRepository::delete);

        return toCommunityPostResponse(post, currentUserId);
    }

    @GetMapping("/communities/{communityId}/posts/{postId}/comments")
    public List<CommunityPostCommentResponse> listPostComments(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String postId) {
        parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID postUuid = parsePrefixedUuid(postId, "POST-");
        CommunityPostJpaEntity post =
                postRepository
                        .findById(postUuid)
                        .filter(p -> community.getId().equals(p.getCommunityId()))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Post no encontrado"));

        return postCommentRepository.findAllByFeedPostIdOrderByCreatedAtAsc(post.getId()).stream()
                .map(this::toCommunityPostCommentResponse)
                .toList();
    }

    @PostMapping("/communities/{communityId}/posts/{postId}/comments")
    @Transactional
    public CommunityPostCommentResponse createPostComment(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String communityId,
            @PathVariable String postId,
            @RequestBody CreatePostCommentRequest request) {
        UUID currentUserId = parseUserId(userId);
        CommunityJpaEntity community = findCommunity(communityId);
        UUID postUuid = parsePrefixedUuid(postId, "POST-");
        CommunityPostJpaEntity post =
                postRepository
                        .findById(postUuid)
                        .filter(p -> community.getId().equals(p.getCommunityId()))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Post no encontrado"));

        if (request == null
                || request.contenido() == null
                || request.contenido().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El comentario no puede estar vacio");
        }

        PostCommentJpaEntity comment = new PostCommentJpaEntity();
        comment.setId(UUID.randomUUID());
        comment.setFeedPostId(post.getId());
        comment.setUserId(currentUserId);
        comment.setCommentBody(request.contenido().trim());
        comment.setStatus("PUBLISHED");
        comment.setCreatedAt(OffsetDateTime.now());
        postCommentRepository.save(comment);

        return toCommunityPostCommentResponse(comment);
    }

    public record CreatePostCommentRequest(String contenido) {}

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
                formatCompanyId(tenant.getId()),
                tenant.getBusinessName(),
                null,
                0.0,
                tenant.getDescription(),
                tenant.getBusinessType());
    }

    private CommunityResponse toCommunityResponse(CommunityJpaEntity community, boolean joined) {
        String creadoEn =
                community.getCreatedAt() != null
                        ? community.getCreatedAt().toInstant().toString()
                        : null;
        return new CommunityResponse(
                formatCommunityId(community.getId()),
                community.getName(),
                community.getDescription(),
                community.getMembersCount(),
                joined,
                creadoEn);
    }

    private CommunityMemberResponse toCommunityMemberResponse(
            CommunityMembershipJpaEntity membership, UUID currentUserId, String role) {
        String memberName =
                userRepository
                        .findById(membership.getUserId())
                        .map(user -> displayName(user.getFirstName(), user.getLastName()))
                        .orElse("Usuario");
        String unidoEn =
                membership.getJoinedAt() != null
                        ? membership.getJoinedAt().toInstant().toString()
                        : null;
        boolean esMiUsuario = membership.getUserId().equals(currentUserId);
        return new CommunityMemberResponse(
                "USR-" + membership.getUserId(), memberName, role, unidoEn, esMiUsuario);
    }

    private CommunityPostResponse toCommunityPostResponse(
            CommunityPostJpaEntity post, UUID currentUserId) {
        String author =
                post.getAuthorUserId() == null
                        ? "Usuario"
                        : userRepository
                                .findById(post.getAuthorUserId())
                                .map(user -> displayName(user.getFirstName(), user.getLastName()))
                                .orElse("Usuario");
        String creadoEn =
                post.getCreatedAt() != null ? post.getCreatedAt().toInstant().toString() : null;
        long likes = postLikeRepository.countByPostId(post.getId());
        long comments = postCommentRepository.countByFeedPostId(post.getId());
        boolean meGusta =
                postLikeRepository.findByPostIdAndUserId(post.getId(), currentUserId).isPresent();
        return new CommunityPostResponse(
                formatPostId(post.getId()),
                author,
                post.getTitle(),
                post.getContent(),
                creadoEn,
                likes,
                comments,
                meGusta);
    }

    private CommunityPostCommentResponse toCommunityPostCommentResponse(
            PostCommentJpaEntity comment) {
        String author =
                comment.getUserId() == null
                        ? "Usuario"
                        : userRepository
                                .findById(comment.getUserId())
                                .map(user -> displayName(user.getFirstName(), user.getLastName()))
                                .orElse("Usuario");
        String creadoEn =
                comment.getCreatedAt() != null
                        ? comment.getCreatedAt().toInstant().toString()
                        : null;
        return new CommunityPostCommentResponse(
                "CMT-" + comment.getId(), author, comment.getCommentBody(), creadoEn);
    }

    /**
     * Resuelve el rol de un miembro: explicit role en la tabla, o fallback al miembro más antiguo
     * que se convierte en ADMIN implícito (garantiza siempre 1 admin).
     */
    private String resolveRole(
            CommunityMembershipJpaEntity membership,
            List<CommunityMembershipJpaEntity> allMemberships) {
        java.util.Optional<CommunityMemberRoleJpaEntity> explicit =
                memberRoleRepository.findByCommunityIdAndUserId(
                        membership.getCommunityId(), membership.getUserId());
        if (explicit.isPresent()) {
            return explicit.get().getRole();
        }

        boolean anyExplicitAdmin =
                memberRoleRepository.countByCommunityIdAndRole(
                                membership.getCommunityId(),
                                CommunityMemberRoleJpaEntity.ROLE_ADMIN)
                        > 0;

        if (!anyExplicitAdmin && allMemberships.size() > 0) {
            CommunityMembershipJpaEntity oldest = allMemberships.get(0);
            if (oldest.getUserId().equals(membership.getUserId())) {
                return CommunityMemberRoleJpaEntity.ROLE_ADMIN;
            }
        }

        return "MEMBER";
    }

    private String roleForUser(UUID userId, List<CommunityMembershipJpaEntity> memberships) {
        return memberships.stream()
                .filter(m -> userId.equals(m.getUserId()))
                .findFirst()
                .map(m -> resolveRole(m, memberships))
                .orElse("NONE");
    }

    private void setMemberRole(UUID communityId, UUID userId, String role) {
        CommunityMemberRoleJpaEntity entity =
                memberRoleRepository
                        .findByCommunityIdAndUserId(communityId, userId)
                        .orElseGet(
                                () -> {
                                    CommunityMemberRoleJpaEntity created =
                                            new CommunityMemberRoleJpaEntity();
                                    created.setId(UUID.randomUUID());
                                    created.setCommunityId(communityId);
                                    created.setUserId(userId);
                                    return created;
                                });
        entity.setRole(role);
        entity.setGrantedAt(OffsetDateTime.now());
        memberRoleRepository.save(entity);
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
