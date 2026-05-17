package com.techmarket.techmarket.company.api.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empresa")
public class CompanyPortalController {

    private static final TypeReference<List<Object>> LIST_TYPE = new TypeReference<>() {};
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    public CompanyPortalController(JdbcTemplate jdbc, ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/perfil")
    public Map<String, Object> profile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        return profileResponse(resolveTenantId(userId, tenantId, companyId));
    }

    @PutMapping("/perfil")
    public Map<String, Object> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        ensureProfile(currentTenantId);
        jdbc.update(
                """
                UPDATE tenants
                SET business_name = COALESCE(?, business_name),
                    business_type = COALESCE(?, business_type),
                    description = COALESCE(?, description),
                    updated_at = ?
                WHERE id = ?
                """,
                stringValue(request.get("name")),
                stringValue(request.get("businessType")),
                stringValue(request.get("description")),
                OffsetDateTime.now(),
                currentTenantId);
        jdbc.update(
                """
                UPDATE tenant_profiles
                SET logo_url = COALESCE(?, logo_url),
                    logo_text = COALESCE(?, logo_text),
                    slogan = COALESCE(?, slogan),
                    specialization = COALESCE(?, specialization),
                    category = COALESCE(?, category),
                    rating_average = COALESCE(?, rating_average),
                    reviews_count = COALESCE(?, reviews_count),
                    experience_years = COALESCE(?, experience_years),
                    short_description = COALESCE(?, short_description),
                    full_description = COALESCE(?, full_description),
                    about = COALESCE(?, about),
                    addresses_json = COALESCE(?, addresses_json),
                    coverage_areas_json = COALESCE(?, coverage_areas_json),
                    contacts_json = COALESCE(?, contacts_json),
                    social_links_json = COALESCE(?, social_links_json),
                    schedules_json = COALESCE(?, schedules_json),
                    branches_json = COALESCE(?, branches_json),
                    location_overview_json = COALESCE(?, location_overview_json),
                    settings_json = COALESCE(?, settings_json)
                WHERE tenant_id = ?
                """,
                stringValue(request.get("logoUrl")),
                stringValue(request.get("logoText")),
                stringValue(request.get("slogan")),
                stringValue(request.get("specialization")),
                stringValue(request.get("category")),
                decimalValue(request.get("rating")),
                integerValue(request.get("reviewCount")),
                integerValue(request.get("experienceYears")),
                stringValue(request.get("description")),
                stringValue(request.get("description")),
                stringValue(request.get("about")),
                jsonValue(request.get("addresses")),
                jsonValue(request.get("coverageAreas")),
                jsonValue(request.get("contacts")),
                jsonValue(request.get("socialLinks")),
                jsonValue(request.get("schedules")),
                jsonValue(request.get("branches")),
                jsonValue(request.get("locationOverview")),
                jsonValue(request.get("settings")),
                currentTenantId);
        return profileResponse(currentTenantId);
    }

    @GetMapping("/resumen")
    public Map<String, Object> summary(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        Map<String, Object> profile = profileResponse(currentTenantId);
        long products = count("SELECT COUNT(*) FROM listings WHERE tenant_id = ?", currentTenantId);
        long activeProducts =
                count(
                        "SELECT COUNT(*) FROM listings WHERE tenant_id = ? AND LOWER(COALESCE(status, '')) IN ('active', 'activo', 'published', 'publicado')",
                        currentTenantId);
        long publications =
                count("SELECT COUNT(*) FROM feed_posts WHERE tenant_id = ?", currentTenantId);
        long reviews = count("SELECT COUNT(*) FROM reviews WHERE tenant_id = ?", currentTenantId);
        BigDecimal avgPrice =
                decimalQuery(
                        "SELECT COALESCE(AVG(base_price), 0) FROM listings WHERE tenant_id = ?",
                        currentTenantId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", profile.get("id"));
        response.put(
                "panel",
                mapOf(
                        "title",
                        "Panel de " + valueOrDefault(profile.get("name"), "empresa"),
                        "subtitle",
                        valueOrDefault(profile.get("slogan"), "Gestiona tu presencia comercial"),
                        "chips",
                        List.of(
                                valueOrDefault(profile.get("businessType"), "empresa"),
                                activeProducts + " activos",
                                reviews + " resenas")));
        response.put(
                "radar",
                List.of(
                        mapOf("label", "Catalogo", "value", boundedScore(products, 20)),
                        mapOf("label", "Actividad", "value", boundedScore(publications, 12)),
                        mapOf("label", "Confianza", "value", boundedScore(reviews, 50)),
                        mapOf("label", "Perfil", "value", profileCompletion(profile))));
        response.put(
                "metrics",
                List.of(
                        metric("productos", "Productos", products, "+" + activeProducts + " activos", "success", "/empresa/publicaciones"),
                        metric("publicaciones", "Publicaciones", publications, "feed actualizado", "info", "/empresa/publicaciones"),
                        metric("resenas", "Resenas", reviews, "promedio " + profile.get("rating"), "warning", "/empresa/perfil"),
                        metric("precio-promedio", "Precio promedio", avgPrice, "catalogo", "neutral", "/empresa/publicaciones")));
        response.put(
                "ai",
                mapOf(
                        "title",
                        "Asistente IA empresarial",
                        "description",
                        "Consulta oportunidades sobre catalogo, perfil y actividad reciente.",
                        "placeholder",
                        "Pregunta por mejoras para vender mas",
                        "recommendedQuestions",
                        List.of(
                                "Que productos deberia destacar esta semana?",
                                "Como puedo mejorar mi perfil?",
                                "Que acciones aumentarian la conversion?")));
        response.put("alerts", alerts(products, activeProducts, profile));
        response.put("recentActivity", recentActivity(currentTenantId));
        response.put("recommendedActions", recommendedActions(products, activeProducts, profile));
        response.put("lastSessionAt", latestTimestamp("SELECT MAX(created_at) FROM interaction_events WHERE tenant_id = ?", currentTenantId));
        response.put("settings", profile.get("settings"));
        return response;
    }

    @PostMapping("/ia/consulta")
    public Map<String, Object> aiQuery(@RequestBody Map<String, Object> request) {
        String question = valueOrDefault(request.get("question"), "Consulta empresarial");
        String context = valueOrDefault(request.get("context"), "perfil y publicaciones");
        Map<String, Object> settings = settings(jsonValue(request.get("settings")));
        return mapOf(
                "summary",
                "Analisis generado para: " + question,
                "dataPoints",
                List.of(
                        "Contexto evaluado: " + context,
                        "Revisa perfil, publicaciones y productos antes de priorizar cambios.",
                        "Las acciones con impacto visible suelen combinar catalogo activo y respuesta rapida."),
                "advice",
                "Prioriza una publicacion clara con precio, imagen y estado activo; luego actualiza cobertura, contactos y horarios para reducir friccion.",
                "nextStep",
                "Publica o actualiza el producto con mayor demanda y comparte una oferta con llamada a contacto.",
                "actionPlan",
                List.of("Actualizar catalogo visible", "Responder conversaciones abiertas", "Revisar resenas pendientes"),
                "watchItems",
                List.of("Productos sin imagen", "Conversaciones sin leer", "Resenas sin respuesta"),
                "priority",
                "medium",
                "confidence",
                0.82,
                "focusLabel",
                "Publicaciones",
                "focusHref",
                "/empresa/publicaciones",
                "settings",
                settings);
    }

    @GetMapping("/publicaciones")
    public Map<String, Object> publications(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        Map<String, Object> company = companySummary(currentTenantId);
        UUID currentUserId = parseOptionalUuid(userId);
        List<Map<String, Object>> feed = feed(currentTenantId, currentUserId);
        List<Map<String, Object>> textPosts = textPosts(currentTenantId, currentUserId);
        List<Map<String, Object>> products = products(currentTenantId, company);
        List<Map<String, Object>> services = services(currentTenantId, company);
        List<Map<String, Object>> offers = offers(currentTenantId, company);
        List<Map<String, Object>> surveys = surveys(currentTenantId);
        List<Map<String, Object>> posts = posts(currentTenantId, currentUserId);
        List<Map<String, Object>> users = interactingUsers(currentTenantId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("company", company);
        response.put(
                "summary",
                mapOf(
                        "visibleContentLabel",
                        "Contenido visible",
                        "currentFilter",
                        "all",
                        "visibleCount",
                        feed.size(),
                        "totalManaged",
                        products.size(),
                        "tip",
                        "Mantener productos activos con imagen mejora la visibilidad."));
        response.put(
                "filters",
                List.of(
                        mapOf("id", "all", "label", "Todos", "value", "all"),
                        mapOf("id", "product", "label", "Productos", "value", "product"),
                        mapOf("id", "service", "label", "Servicios", "value", "service"),
                        mapOf("id", "offer", "label", "Ofertas", "value", "offer"),
                        mapOf("id", "survey", "label", "Encuestas", "value", "survey"),
                        mapOf("id", "text", "label", "Texto", "value", "text"),
                        mapOf("id", "post", "label", "Publicaciones", "value", "post")));
        response.put("feed", feed);
        response.put("textPosts", textPosts);
        response.put("products", products);
        response.put("services", services);
        response.put("offers", offers);
        response.put("surveys", surveys);
        response.put("posts", posts);
        response.put("users", users);
        response.put("interactingUsers", users);
        response.put("latestInteractionNotification", latestInteractionNotification(currentTenantId));
        response.put("settings", profileResponse(currentTenantId).get("settings"));
        return response;
    }

    @PostMapping("/publicaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createPublication(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID listingId = null;
        String type = valueOrDefault(request.get("type"), "post").toLowerCase(Locale.ROOT);
        if ("product".equals(type) || "service".equals(type) || "offer".equals(type)) {
            listingId = UUID.randomUUID();
            jdbc.update(
                    """
                    INSERT INTO listings (
                        id, tenant_id, category_id, listing_type, title, description, base_price,
                        currency, status, is_visible, image_url, previous_price, label, created_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    listingId,
                    currentTenantId,
                    firstCategoryId(),
                    listingTypeForPublication(type),
                    stringValue(request.get("title")),
                    stringValue(request.get("description")),
                    requestPrice(request),
                    "BOB",
                    valueOrDefault(request.get("status"), "ACTIVE"),
                    true,
                    stringValue(request.get("imageUrl")),
                    decimalValue(request.get("previousPrice")),
                    stringValue(request.get("label")),
                    OffsetDateTime.now(),
                    OffsetDateTime.now());
        }
        UUID postId = UUID.randomUUID();
        jdbc.update(
                """
                INSERT INTO feed_posts (
                    id, tenant_id, author_user_id, post_type, title, content, status, created_at,
                    image_url, price, previous_price, label, options_json, company_listing_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                postId,
                currentTenantId,
                parseOptionalUuid(userId),
                type,
                stringValue(request.get("title")),
                stringValue(request.get("description")),
                valueOrDefault(request.get("status"), "PUBLISHED"),
                OffsetDateTime.now(),
                stringValue(request.get("imageUrl")),
                requestPrice(request),
                decimalValue(request.get("previousPrice")),
                stringValue(request.get("label")),
                jsonValue(request.get("options")),
                listingId);
        if ("text".equals(type)) {
            return findTextPostItem(postId, parseOptionalUuid(userId));
        }
        if ("service".equals(type)) {
            return service(listingId, companySummary(currentTenantId));
        }
        if ("offer".equals(type)) {
            return offer(listingId, companySummary(currentTenantId));
        }
        return findFeedItem(postId, parseOptionalUuid(userId));
    }

    @GetMapping("/productos")
    public Map<String, Object> products(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        return mapOf("products", products(currentTenantId, companySummary(currentTenantId)));
    }

    @GetMapping("/servicios")
    public Map<String, Object> services(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        return mapOf("services", services(currentTenantId, companySummary(currentTenantId)));
    }

    @GetMapping("/ofertas")
    public Map<String, Object> offers(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        return mapOf("offers", offers(currentTenantId, companySummary(currentTenantId)));
    }

    @PostMapping("/encuestas")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createSurvey(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID postId = UUID.randomUUID();
        jdbc.update(
                """
                INSERT INTO feed_posts (
                    id, tenant_id, author_user_id, post_type, title, content, status, created_at,
                    image_url, price, previous_price, label, options_json, company_listing_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                postId,
                currentTenantId,
                parseOptionalUuid(userId),
                "survey",
                stringValue(request.get("question")),
                stringValue(request.get("question")),
                "PUBLISHED",
                OffsetDateTime.now(),
                null,
                null,
                null,
                "Encuesta",
                jsonValue(request.get("options")),
                null);
        return survey(postId);
    }

    @PostMapping("/publicaciones/{publicationId}/likes")
    public Map<String, Object> likePublication(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String publicationId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID postId = parsePrefixedUuid(valueOrDefault(request.get("publicationId"), publicationId), "POST-");
        ensurePublication(currentTenantId, postId);
        UUID currentUserId = parseOptionalUuid(userId);
        boolean liked = booleanValue(request.get("liked"));
        Optional<UUID> existing = existingLike(postId, currentUserId);
        if (existing.isPresent()) {
            jdbc.update(
                    "UPDATE company_publication_likes SET liked = ?, updated_at = ? WHERE id = ?",
                    liked,
                    OffsetDateTime.now(),
                    existing.get());
        } else {
            jdbc.update(
                    "INSERT INTO company_publication_likes (id, feed_post_id, user_id, tenant_id, liked, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    UUID.randomUUID(),
                    postId,
                    currentUserId,
                    currentTenantId,
                    liked,
                    OffsetDateTime.now(),
                    OffsetDateTime.now());
        }
        return mapOf(
                "publicationId", "POST-" + postId,
                "liked", liked,
                "likes", likesCount(postId),
                "settings", settings(jsonValue(request.get("settings"))));
    }

    @PostMapping("/publicaciones/{publicationId}/comentarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> commentPublication(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String publicationId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID postId = parsePrefixedUuid(valueOrDefault(request.get("publicationId"), publicationId), "POST-");
        ensurePublication(currentTenantId, postId);
        UUID commentId = UUID.randomUUID();
        jdbc.update(
                "INSERT INTO post_comments (id, feed_post_id, user_id, comment_body, status, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                commentId,
                postId,
                parseOptionalUuid(userId),
                valueOrDefault(request.get("text"), ""),
                "ACTIVE",
                OffsetDateTime.now());
        return mapOf(
                "id",
                "CMT-" + commentId,
                "publicationId",
                "POST-" + postId,
                "authorId",
                stringValue(request.get("authorId")),
                "authorName",
                valueOrDefault(request.get("authorName"), "Usuario"),
                "text",
                valueOrDefault(request.get("text"), ""),
                "createdAt",
                OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "settings",
                settings(jsonValue(request.get("settings"))));
    }

    @PutMapping("/productos/{productId}")
    public Map<String, Object> updateProduct(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String productId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID listingId = parsePrefixedUuid(valueOrDefault(request.get("id"), productId), "PROD-");
        updateListingPublication(currentTenantId, listingId, "PRODUCT", request, "Product not found");
        return product(listingId, companySummary(currentTenantId));
    }

    @PutMapping("/servicios/{serviceId}")
    public Map<String, Object> updateService(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String serviceId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID listingId = parsePrefixedUuid(valueOrDefault(request.get("id"), serviceId), "SERV-");
        updateListingPublication(currentTenantId, listingId, "SERVICE", request, "Service not found");
        return service(listingId, companySummary(currentTenantId));
    }

    @PutMapping("/ofertas/{offerId}")
    public Map<String, Object> updateOffer(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String offerId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID listingId = parsePrefixedUuid(valueOrDefault(request.get("id"), offerId), "OFF-");
        updateListingPublication(currentTenantId, listingId, "OFFER", request, "Offer not found");
        return offer(listingId, companySummary(currentTenantId));
    }

    private void updateListingPublication(
            UUID currentTenantId,
            UUID listingId,
            String listingType,
            Map<String, Object> request,
            String notFoundMessage) {
        int updated =
                jdbc.update(
                        """
                        UPDATE listings
                        SET title = COALESCE(?, title),
                            description = COALESCE(?, description),
                            base_price = COALESCE(?, base_price),
                            status = COALESCE(?, status),
                            image_url = COALESCE(?, image_url),
                            previous_price = COALESCE(?, previous_price),
                            label = COALESCE(?, label),
                            updated_at = ?
                        WHERE id = ? AND tenant_id = ? AND UPPER(COALESCE(listing_type, '')) = ?
                        """,
                        requestTitle(request),
                        stringValue(request.get("description")),
                        requestPrice(request),
                        stringValue(request.get("status")),
                        stringValue(request.get("imageUrl")),
                        decimalValue(request.get("previousPrice")),
                        stringValue(request.get("label")),
                        OffsetDateTime.now(),
                        listingId,
                        currentTenantId,
                        listingType);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage);
        }
        jdbc.update(
                """
                UPDATE feed_posts
                SET title = COALESCE(?, title),
                    content = COALESCE(?, content),
                    price = COALESCE(?, price),
                    status = COALESCE(?, status),
                    image_url = COALESCE(?, image_url),
                    previous_price = COALESCE(?, previous_price),
                    label = COALESCE(?, label)
                WHERE company_listing_id = ? AND tenant_id = ?
                """,
                requestTitle(request),
                stringValue(request.get("description")),
                requestPrice(request),
                stringValue(request.get("status")),
                stringValue(request.get("imageUrl")),
                decimalValue(request.get("previousPrice")),
                stringValue(request.get("label")),
                listingId,
                currentTenantId);
    }

    @PostMapping("/archivos/imagenes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadImage(@RequestBody Map<String, Object> request) {
        return imageUploadResponse(
                valueOrDefault(request.get("folder"), "empresa"), valueOrDefault(request.get("file"), ""));
    }

    @PostMapping(value = "/archivos/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadMultipartImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "empresa") String folder) {
        return imageUploadResponse(
                folder, file.getOriginalFilename(), file.getContentType(), file.getSize());
    }

    @GetMapping("/chat/conversaciones")
    public Map<String, Object> chatConversations(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        List<Map<String, Object>> conversations = conversations(currentTenantId);
        long unreadTotal =
                conversations.stream()
                        .filter(item -> Boolean.TRUE.equals(item.get("unread")))
                        .count();
        return mapOf("conversations", conversations, "unreadTotal", unreadTotal, "settings", profileResponse(currentTenantId).get("settings"));
    }

    @GetMapping("/chat/conversaciones/{conversationId}/mensajes")
    public Map<String, Object> chatMessages(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String conversationId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID ticketId = parsePrefixedUuid(conversationId, "CONV-");
        Map<String, Object> ticket = findCompanyTicket(currentTenantId, ticketId);
        return mapOf(
                "conversationId", "CONV-" + ticketId,
                "customer", userSummary((UUID) ticket.get("customer_user_id")),
                "productOrService", listingSummary((UUID) ticket.get("listing_id")),
                "messages", messages(ticketId),
                "settings", profileResponse(currentTenantId).get("settings"));
    }

    @PostMapping("/chat/conversaciones/{conversationId}/mensajes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> sendChatMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String conversationId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID ticketId = parsePrefixedUuid(valueOrDefault(request.get("conversationId"), conversationId), "CONV-");
        findCompanyTicket(currentTenantId, ticketId);
        UUID messageId = UUID.randomUUID();
        jdbc.update(
                "INSERT INTO ticket_messages (id, ticket_id, author_user_id, message_body, message_type, is_visible_to_customer, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                messageId,
                ticketId,
                parseOptionalUuid(userId),
                valueOrDefault(request.get("text"), ""),
                valueOrDefault(request.get("author"), "company"),
                true,
                OffsetDateTime.now());
        return mapOf(
                "id", "MSG-" + messageId,
                "conversationId", "CONV-" + ticketId,
                "author", valueOrDefault(request.get("author"), "company"),
                "text", valueOrDefault(request.get("text"), ""),
                "createdAt", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "settings", settings(jsonValue(request.get("settings"))));
    }

    @PatchMapping("/chat/conversaciones/{conversationId}/leido")
    public Map<String, Object> markChatRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String conversationId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID ticketId = parsePrefixedUuid(valueOrDefault(request.get("conversationId"), conversationId), "CONV-");
        findCompanyTicket(currentTenantId, ticketId);
        UUID currentUserId = parseOptionalUuid(userId);
        boolean read = booleanValue(request.get("read"));
        if (currentUserId != null && read) {
            upsertReadReceipt(ticketId, currentUserId);
        }
        return mapOf("conversationId", "CONV-" + ticketId, "read", read, "settings", settings(jsonValue(request.get("settings"))));
    }

    @GetMapping("/resenas")
    public Map<String, Object> reviews(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        List<Map<String, Object>> reviews = reviewsList(currentTenantId);
        BigDecimal average = decimalQuery("SELECT COALESCE(AVG(rating), 0) FROM reviews WHERE tenant_id = ?", currentTenantId);
        long pendingReplies = count("SELECT COUNT(*) FROM reviews WHERE tenant_id = ? AND company_response IS NULL", currentTenantId);
        return mapOf(
                "reviews", reviews,
                "totalReviews", reviews.size(),
                "averageStars", average,
                "pendingReplies", pendingReplies,
                "followUpItems", followUpItems(currentTenantId),
                "topTags", List.of("atencion", "calidad", "precio"),
                "ratingSummary", ratingSummary(currentTenantId),
                "settings", profileResponse(currentTenantId).get("settings"));
    }

    @PostMapping("/resenas/{reviewId}/respuesta")
    public Map<String, Object> replyReview(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @PathVariable String reviewId,
            @RequestBody Map<String, Object> request) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        UUID reviewUuid = parsePrefixedUuid(valueOrDefault(request.get("reviewId"), reviewId), "REV-");
        int updated =
                jdbc.update(
                        "UPDATE reviews SET company_response = ?, company_response_at = ?, updated_at = ? WHERE id = ? AND tenant_id = ?",
                        stringValue(request.get("response")),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        reviewUuid,
                        currentTenantId);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }
        return mapOf(
                "reviewId", "REV-" + reviewUuid,
                "response", stringValue(request.get("response")),
                "wasResponded", booleanValue(request.get("wasResponded")) || stringValue(request.get("response")) != null,
                "settings", settings(jsonValue(request.get("settings"))));
    }

    @GetMapping("/analiticas")
    public Map<String, Object> analytics(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Company-Id", required = false) String companyId) {
        UUID currentTenantId = resolveTenantId(userId, tenantId, companyId);
        BigDecimal ratingAverage = decimalQuery("SELECT COALESCE(AVG(rating), 0) FROM reviews WHERE tenant_id = ?", currentTenantId);
        long visits = count("SELECT COUNT(*) FROM interaction_events WHERE tenant_id = ?", currentTenantId);
        long conversions = count("SELECT COALESCE(SUM(conversions_count), 0) FROM tenant_daily_metrics WHERE tenant_id = ?", currentTenantId);
        long leads = count("SELECT COALESCE(SUM(leads_count), 0) FROM tenant_daily_metrics WHERE tenant_id = ?", currentTenantId);
        BigDecimal conversion =
                leads == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(conversions * 100.0 / leads);
        return mapOf(
                "publicationMetrics", publicationMetrics(currentTenantId),
                "ratingLevels", ratingSummary(currentTenantId),
                "userReviews", reviewsList(currentTenantId),
                "userComments", userComments(currentTenantId),
                "growthSeries", growthSeries(currentTenantId),
                "totalVisits", visits,
                "averageConversion", conversion,
                "ratingAverage", ratingAverage,
                "growthIndex", boundedScore(conversions + visits, 100),
                "settings", profileResponse(currentTenantId).get("settings"));
    }

    private Map<String, Object> profileResponse(UUID tenantId) {
        ensureProfile(tenantId);
        Map<String, Object> row =
                jdbc.queryForMap(
                        """
                        SELECT t.id, t.business_name, t.business_type, t.description,
                               tp.logo_url, tp.logo_text, tp.slogan, tp.specialization, tp.category,
                               tp.rating_average, tp.reviews_count, tp.experience_years,
                               tp.short_description, tp.full_description, tp.about,
                               tp.addresses_json, tp.coverage_areas_json, tp.contacts_json,
                               tp.social_links_json, tp.schedules_json, tp.branches_json,
                               tp.location_overview_json, tp.settings_json
                        FROM tenants t
                        LEFT JOIN tenant_profiles tp ON tp.tenant_id = t.id
                        WHERE t.id = ?
                        """,
                        tenantId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", "EMP-" + tenantId);
        response.put("name", row.get("business_name"));
        response.put("logoUrl", row.get("logo_url"));
        response.put("logoText", valueOrDefault(row.get("logo_text"), initials(row.get("business_name"))));
        response.put("slogan", row.get("slogan"));
        response.put("specialization", row.get("specialization"));
        response.put("category", row.get("category"));
        response.put("businessType", row.get("business_type"));
        response.put("rating", decimalOrDefault(row.get("rating_average"), BigDecimal.ZERO));
        response.put("reviewCount", numberOrDefault(row.get("reviews_count"), 0));
        response.put("experienceYears", numberOrDefault(row.get("experience_years"), 0));
        response.put("description", valueOrDefault(row.get("description"), row.get("short_description")));
        response.put("about", valueOrDefault(row.get("about"), row.get("full_description")));
        response.put("addresses", jsonList(row.get("addresses_json")));
        response.put("coverageAreas", jsonList(row.get("coverage_areas_json")));
        response.put("contacts", jsonList(row.get("contacts_json")));
        response.put("socialLinks", jsonList(row.get("social_links_json")));
        response.put("schedules", jsonList(row.get("schedules_json")));
        response.put("branches", branches(tenantId, row.get("branches_json")));
        response.put("locationOverview", jsonMap(row.get("location_overview_json")));
        response.put("settings", settings(row.get("settings_json")));
        return response;
    }

    private Map<String, Object> imageUploadResponse(String folderValue, String fileValue) {
        return imageUploadResponse(folderValue, fileValue, null, 0L);
    }

    private Map<String, Object> imageUploadResponse(
            String folderValue, String fileValue, String mimeType, long size) {
        String folder = valueOrDefault(folderValue, "empresa").replaceAll("[^a-zA-Z0-9_-]", "-");
        String file = valueOrDefault(fileValue, UUID.randomUUID().toString());
        String extension = file.contains(".") ? file.substring(file.lastIndexOf('.')) : ".jpg";
        UUID imageId = UUID.randomUUID();
        String url =
                "https://cdn.techmarket.local/empresa/"
                        + folder
                        + "/"
                        + imageId
                        + extension;
        return mapOf(
                "id", "IMG-" + imageId,
                "fileName", file,
                "url", url,
                "mimeType", valueOrDefault(mimeType, "image/jpeg"),
                "size", size);
    }

    private UUID resolveTenantId(String userId, String tenantId, String companyId) {
        String direct = tenantId == null || tenantId.isBlank() ? companyId : tenantId;
        if (direct != null && !direct.isBlank()) {
            UUID parsed = parsePrefixedUuid(direct, "EMP-");
            if (count("SELECT COUNT(*) FROM tenants WHERE id = ?", parsed) == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
            }
            return parsed;
        }
        UUID currentUserId = parseRequiredUserId(userId);
        try {
            return jdbc.queryForObject(
                    """
                    SELECT tenant_id
                    FROM tenant_members
                    WHERE user_id = ? AND LOWER(COALESCE(status, 'active')) IN ('active', 'activo')
                    ORDER BY joined_at DESC NULLS LAST
                    LIMIT 1
                    """,
                    UUID.class,
                    currentUserId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company membership not found");
        }
    }

    private void ensureProfile(UUID tenantId) {
        if (count("SELECT COUNT(*) FROM tenant_profiles WHERE tenant_id = ?", tenantId) > 0) {
            return;
        }
        jdbc.update(
                "INSERT INTO tenant_profiles (id, tenant_id, logo_text, settings_json) VALUES (?, ?, ?, ?)",
                UUID.randomUUID(),
                tenantId,
                initials(companyName(tenantId)),
                "{\"editable\":true}");
    }

    private List<Map<String, Object>> feed(UUID tenantId, UUID userId) {
        return jdbc.queryForList(
                        """
                        SELECT id
                        FROM feed_posts
                        WHERE tenant_id = ?
                        ORDER BY created_at DESC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(row -> findFeedItem((UUID) row.get("id"), userId))
                .toList();
    }

    private List<Map<String, Object>> textPosts(UUID tenantId, UUID userId) {
        return jdbc.queryForList(
                        """
                        SELECT id
                        FROM feed_posts
                        WHERE tenant_id = ? AND LOWER(COALESCE(post_type, '')) = 'text'
                        ORDER BY created_at DESC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(row -> findTextPostItem((UUID) row.get("id"), userId))
                .toList();
    }

    private List<Map<String, Object>> posts(UUID tenantId, UUID userId) {
        return jdbc.queryForList(
                        """
                        SELECT id
                        FROM feed_posts
                        WHERE tenant_id = ?
                          AND LOWER(COALESCE(post_type, 'post')) NOT IN ('product', 'service', 'offer', 'survey')
                        ORDER BY created_at DESC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(row -> findFeedItem((UUID) row.get("id"), userId))
                .toList();
    }

    private List<Map<String, Object>> interactingUsers(UUID tenantId) {
        Map<UUID, Map<String, Object>> users = new LinkedHashMap<>();
        jdbc.queryForList(
                        """
                        SELECT DISTINCT user_id
                        FROM interaction_events
                        WHERE tenant_id = ? AND user_id IS NOT NULL
                        LIMIT 20
                        """,
                        tenantId)
                .forEach(row -> putUser(users, (UUID) row.get("user_id")));
        jdbc.queryForList(
                        """
                        SELECT DISTINCT user_id
                        FROM reviews
                        WHERE tenant_id = ? AND user_id IS NOT NULL
                        LIMIT 20
                        """,
                        tenantId)
                .forEach(row -> putUser(users, (UUID) row.get("user_id")));
        jdbc.queryForList(
                        """
                        SELECT DISTINCT pc.user_id
                        FROM post_comments pc
                        JOIN feed_posts fp ON fp.id = pc.feed_post_id
                        WHERE fp.tenant_id = ? AND pc.user_id IS NOT NULL
                        LIMIT 20
                        """,
                        tenantId)
                .forEach(row -> putUser(users, (UUID) row.get("user_id")));
        return new ArrayList<>(users.values());
    }

    private void putUser(Map<UUID, Map<String, Object>> users, UUID userId) {
        if (userId != null && !users.containsKey(userId)) {
            users.put(userId, userSummary(userId));
        }
    }

    private Map<String, Object> latestInteractionNotification(UUID tenantId) {
        try {
            Map<String, Object> row =
                    jdbc.queryForMap(
                            """
                            SELECT id, event_type, created_at
                            FROM interaction_events
                            WHERE tenant_id = ?
                            ORDER BY created_at DESC NULLS LAST
                            LIMIT 1
                            """,
                            tenantId);
            return mapOf(
                    "id", "INT-" + row.get("id"),
                    "title", "Nueva interaccion",
                    "detail", valueOrDefault(row.get("event_type"), "Actividad reciente"),
                    "time", row.get("created_at"));
        } catch (EmptyResultDataAccessException ex) {
            return new LinkedHashMap<>();
        }
    }

    private Map<String, Object> findFeedItem(UUID postId, UUID userId) {
        Map<String, Object> row =
                jdbc.queryForMap(
                        """
                        SELECT fp.*, t.business_name, tp.logo_url, tp.logo_text
                        FROM feed_posts fp
                        JOIN tenants t ON t.id = fp.tenant_id
                        LEFT JOIN tenant_profiles tp ON tp.tenant_id = t.id
                        WHERE fp.id = ?
                        """,
                        postId);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", "POST-" + row.get("id"));
        item.put("type", valueOrDefault(row.get("post_type"), "post"));
        item.put("authorName", row.get("business_name"));
        item.put("authorLogoUrl", row.get("logo_url"));
        item.put("authorLogoText", valueOrDefault(row.get("logo_text"), initials(row.get("business_name"))));
        item.put("date", row.get("created_at"));
        item.put("tag", tagFor(row));
        item.put("title", row.get("title"));
        item.put("description", row.get("content"));
        item.put("imageUrl", row.get("image_url"));
        item.put("price", row.get("price"));
        item.put("previousPrice", row.get("previous_price"));
        item.put("status", row.get("status"));
        item.put("label", row.get("label"));
        item.put("options", jsonList(row.get("options_json")));
        item.put("votes", 0);
        item.put(
                "actions",
                mapOf(
                        "canContact", true,
                        "canView", true,
                        "canLike", true,
                        "canComment", true,
                        "canSend", true));
        item.put(
                "social",
                mapOf(
                        "likes",
                        likesCount(postId),
                        "liked",
                        userId != null && likedBy(postId, userId),
                        "commentsCount",
                        commentsCount(postId)));
        item.put(
                "metrics",
                List.of(
                        mapOf("label", "Interacciones", "value", likesCount(postId) + commentsCount(postId), "trend", "estable"),
                        mapOf("label", "Comentarios", "value", commentsCount(postId), "trend", "reciente")));
        return item;
    }

    private Map<String, Object> findTextPostItem(UUID postId, UUID userId) {
        Map<String, Object> row =
                jdbc.queryForMap(
                        """
                        SELECT fp.*, t.business_name, tp.logo_url, tp.logo_text
                        FROM feed_posts fp
                        JOIN tenants t ON t.id = fp.tenant_id
                        LEFT JOIN tenant_profiles tp ON tp.tenant_id = t.id
                        WHERE fp.id = ?
                        """,
                        postId);
        return mapOf(
                "id",
                "POST-" + row.get("id"),
                "title",
                row.get("title"),
                "message",
                row.get("content"),
                "date",
                row.get("created_at"),
                "imageUrl",
                row.get("image_url"),
                "company",
                mapOf(
                        "id",
                        "EMP-" + row.get("tenant_id"),
                        "name",
                        row.get("business_name"),
                        "logoUrl",
                        row.get("logo_url"),
                        "logoText",
                        valueOrDefault(row.get("logo_text"), initials(row.get("business_name")))),
                "social",
                mapOf(
                        "likes",
                        likesCount(postId),
                        "liked",
                        userId != null && likedBy(postId, userId),
                        "commentsCount",
                        commentsCount(postId)),
                "actions",
                textPostActions());
    }

    private List<Map<String, Object>> products(UUID tenantId, Map<String, Object> company) {
        return listingItems(tenantId, company, "PRODUCT").stream().map(row -> product(row, company)).toList();
    }

    private List<Map<String, Object>> services(UUID tenantId, Map<String, Object> company) {
        return listingItems(tenantId, company, "SERVICE").stream().map(row -> service(row, company)).toList();
    }

    private List<Map<String, Object>> offers(UUID tenantId, Map<String, Object> company) {
        return listingItems(tenantId, company, "OFFER").stream().map(row -> offer(row, company)).toList();
    }

    private List<Map<String, Object>> listingItems(
            UUID tenantId, Map<String, Object> company, String listingType) {
        return jdbc.queryForList(
                        """
                        SELECT l.*, fp.id AS publication_id, fp.post_type, fp.created_at AS publication_date,
                               fp.title AS publication_title, fp.content AS publication_description,
                               fp.image_url AS publication_image_url
                        FROM listings l
                        LEFT JOIN feed_posts fp ON fp.company_listing_id = l.id
                        WHERE l.tenant_id = ? AND UPPER(COALESCE(l.listing_type, '')) = ?
                        ORDER BY l.created_at DESC NULLS LAST
                        """,
                        tenantId,
                        listingType);
    }

    private Map<String, Object> product(UUID productId, Map<String, Object> company) {
        return product(listingItem(productId), company);
    }

    private Map<String, Object> product(Map<String, Object> row, Map<String, Object> company) {
        Map<String, Object> product = listingResponse(row, company, "PROD-");
        product.put("status", row.get("status"));
        return product;
    }

    private Map<String, Object> service(UUID serviceId, Map<String, Object> company) {
        return service(listingItem(serviceId), company);
    }

    private Map<String, Object> service(Map<String, Object> row, Map<String, Object> company) {
        return listingResponse(row, company, "SERV-");
    }

    private Map<String, Object> offer(UUID offerId, Map<String, Object> company) {
        return offer(listingItem(offerId), company);
    }

    private Map<String, Object> listingItem(UUID listingId) {
        return jdbc.queryForMap(
                """
                SELECT l.*, fp.id AS publication_id, fp.post_type, fp.created_at AS publication_date,
                       fp.title AS publication_title, fp.content AS publication_description,
                       fp.image_url AS publication_image_url
                FROM listings l
                LEFT JOIN feed_posts fp ON fp.company_listing_id = l.id
                WHERE l.id = ?
                """,
                listingId);
    }

    private Map<String, Object> offer(Map<String, Object> row, Map<String, Object> company) {
        Map<String, Object> offer = new LinkedHashMap<>();
        offer.put("id", "OFF-" + row.get("id"));
        offer.put("title", row.get("title"));
        offer.put("description", row.get("description"));
        offer.put("currentPrice", row.get("base_price"));
        offer.put("previousPrice", row.get("previous_price"));
        offer.put("label", row.get("label"));
        offer.put("imageUrl", row.get("image_url"));
        offer.put("company", company);
        offer.put("publication", publication(row));
        offer.put("metrics", listingMetrics(row));
        return offer;
    }

    private Map<String, Object> listingResponse(
            Map<String, Object> row, Map<String, Object> company, String prefix) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", prefix + row.get("id"));
        item.put("name", row.get("title"));
        item.put("description", row.get("description"));
        item.put("price", row.get("base_price"));
        item.put("imageUrl", row.get("image_url"));
        item.put("company", company);
        item.put("publication", publication(row));
        item.put("metrics", listingMetrics(row));
        return item;
    }

    private Map<String, Object> publication(Map<String, Object> row) {
        return mapOf(
                "id",
                row.get("publication_id") == null ? null : "POST-" + row.get("publication_id"),
                "type",
                valueOrDefault(row.get("post_type"), row.get("listing_type")),
                "tag",
                row.get("label"),
                "date",
                row.get("publication_date"),
                "title",
                valueOrDefault(row.get("publication_title"), row.get("title")),
                "description",
                valueOrDefault(row.get("publication_description"), row.get("description")),
                "imageUrl",
                valueOrDefault(row.get("publication_image_url"), row.get("image_url")));
    }

    private List<Map<String, Object>> listingMetrics(Map<String, Object> row) {
        return List.of(
                mapOf("label", "Estado", "value", valueOrDefault(row.get("status"), "ACTIVE"), "trend", "visible"),
                mapOf("label", "Precio", "value", row.get("base_price"), "trend", "actual"));
    }

    private List<Map<String, Object>> surveys(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT id
                        FROM feed_posts
                        WHERE tenant_id = ? AND LOWER(COALESCE(post_type, '')) = 'survey'
                        ORDER BY created_at DESC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(row -> survey((UUID) row.get("id")))
                .toList();
    }

    private Map<String, Object> survey(UUID postId) {
        Map<String, Object> row =
                jdbc.queryForMap(
                        """
                        SELECT fp.*, t.business_name, tp.logo_url, tp.logo_text
                        FROM feed_posts fp
                        JOIN tenants t ON t.id = fp.tenant_id
                        LEFT JOIN tenant_profiles tp ON tp.tenant_id = t.id
                        WHERE fp.id = ?
                        """,
                        postId);
        List<Map<String, Object>> options = surveyOptions(row.get("options_json"));
        long votes = options.stream().mapToLong(option -> ((Number) option.get("votes")).longValue()).sum();
        return mapOf(
                "id",
                "SURV-" + row.get("id"),
                "question",
                valueOrDefault(row.get("title"), row.get("content")),
                "votes",
                votes,
                "company",
                mapOf(
                        "id",
                        "EMP-" + row.get("tenant_id"),
                        "name",
                        row.get("business_name"),
                        "logoUrl",
                        row.get("logo_url"),
                        "logoText",
                        valueOrDefault(row.get("logo_text"), initials(row.get("business_name")))),
                "publication",
                mapOf(
                        "id",
                        "POST-" + row.get("id"),
                        "type",
                        valueOrDefault(row.get("post_type"), "survey"),
                        "tag",
                        valueOrDefault(row.get("label"), "Encuesta"),
                        "date",
                        row.get("created_at"),
                        "title",
                        valueOrDefault(row.get("title"), row.get("content"))),
                "options",
                options);
    }

    private List<Map<String, Object>> surveyOptions(Object optionsJson) {
        List<Object> rawOptions = jsonList(optionsJson);
        int count = Math.max(rawOptions.size(), 1);
        List<Map<String, Object>> options = new ArrayList<>();
        for (int index = 0; index < rawOptions.size(); index++) {
            Object raw = rawOptions.get(index);
            String text =
                    raw instanceof Map<?, ?> map
                            ? valueOrDefault(map.get("text"), map.get("label"))
                            : valueOrDefault(raw, "Opcion " + (index + 1));
            int votes =
                    raw instanceof Map<?, ?> map
                            ? numberOrDefault(map.get("votes"), 0)
                            : 0;
            options.add(
                    mapOf(
                            "id",
                            "OPT-" + (index + 1),
                            "text",
                            text,
                            "percent",
                            count == 0 ? 0 : 0,
                            "votes",
                            votes));
        }
        long totalVotes = options.stream().mapToLong(option -> ((Number) option.get("votes")).longValue()).sum();
        if (totalVotes > 0) {
            options.forEach(
                    option ->
                            option.put(
                                    "percent",
                                    Math.round((((Number) option.get("votes")).doubleValue() * 100.0) / totalVotes)));
        }
        return options;
    }

    private Map<String, Object> textPostActions() {
        return mapOf(
                "canContact", true,
                "canLike", true,
                "canComment", true,
                "canSend", true);
    }

    private List<Map<String, Object>> conversations(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT t.*, MAX(tm.created_at) AS last_message_at, COUNT(tm.id) AS message_count
                        FROM tickets t
                        LEFT JOIN ticket_messages tm ON tm.ticket_id = t.id
                        WHERE t.tenant_id = ?
                          AND LOWER(COALESCE(t.ticket_type, '')) IN ('company_chat', 'chat', 'support')
                        GROUP BY t.id
                        ORDER BY COALESCE(MAX(tm.created_at), t.created_at, t.opened_at) DESC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id", "CONV-" + row.get("id"),
                                        "subject", valueOrDefault(row.get("subject"), "Conversacion"),
                                        "status", row.get("status"),
                                        "customer", userSummary((UUID) row.get("customer_user_id")),
                                        "productOrService", listingSummary((UUID) row.get("listing_id")),
                                        "lastMessageAt", row.get("last_message_at"),
                                        "messageCount", numberOrDefault(row.get("message_count"), 0),
                                        "unread", true))
                .toList();
    }

    private Map<String, Object> findCompanyTicket(UUID tenantId, UUID ticketId) {
        try {
            return jdbc.queryForMap(
                    """
                    SELECT *
                    FROM tickets
                    WHERE id = ? AND tenant_id = ?
                      AND LOWER(COALESCE(ticket_type, '')) IN ('company_chat', 'chat', 'support')
                    """,
                    ticketId,
                    tenantId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }
    }

    private List<Map<String, Object>> messages(UUID ticketId) {
        return jdbc.queryForList(
                        """
                        SELECT id, author_user_id, message_body, message_type, created_at
                        FROM ticket_messages
                        WHERE ticket_id = ?
                        ORDER BY created_at ASC NULLS LAST
                        """,
                        ticketId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id", "MSG-" + row.get("id"),
                                        "authorId", row.get("author_user_id"),
                                        "author", valueOrDefault(row.get("message_type"), "user"),
                                        "text", row.get("message_body"),
                                        "createdAt", row.get("created_at")))
                .toList();
    }

    private void upsertReadReceipt(UUID ticketId, UUID userId) {
        jdbc.update(
                """
                INSERT INTO chat_read_receipts (id, ticket_id, user_id, read_at)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (ticket_id, user_id) DO UPDATE SET read_at = EXCLUDED.read_at
                """,
                UUID.randomUUID(),
                ticketId,
                userId,
                OffsetDateTime.now());
    }

    private Map<String, Object> userSummary(UUID userId) {
        if (userId == null) {
            return new LinkedHashMap<>();
        }
        try {
            Map<String, Object> row =
                    jdbc.queryForMap(
                            "SELECT id, first_name, last_name, email, phone FROM users WHERE id = ?",
                            userId);
            String name =
                    (valueOrDefault(row.get("first_name"), "") + " " + valueOrDefault(row.get("last_name"), ""))
                            .trim();
            return mapOf(
                    "id", "USR-" + row.get("id"),
                    "name", name.isBlank() ? valueOrDefault(row.get("email"), "Usuario") : name,
                    "email", row.get("email"),
                    "phone", row.get("phone"));
        } catch (EmptyResultDataAccessException ex) {
            return mapOf("id", "USR-" + userId, "name", "Usuario");
        }
    }

    private Map<String, Object> listingSummary(UUID listingId) {
        if (listingId == null) {
            return new LinkedHashMap<>();
        }
        try {
            Map<String, Object> row =
                    jdbc.queryForMap(
                            "SELECT id, title, listing_type, base_price, image_url FROM listings WHERE id = ?",
                            listingId);
            return mapOf(
                    "id", formatListingId(row),
                    "name", row.get("title"),
                    "type", row.get("listing_type"),
                    "price", row.get("base_price"),
                    "imageUrl", row.get("image_url"));
        } catch (EmptyResultDataAccessException ex) {
            return new LinkedHashMap<>();
        }
    }

    private String formatListingId(Map<String, Object> row) {
        String type = valueOrDefault(row.get("listing_type"), "PRODUCT").toUpperCase(Locale.ROOT);
        String prefix =
                switch (type) {
                    case "SERVICE" -> "SERV-";
                    case "OFFER" -> "OFF-";
                    default -> "PROD-";
                };
        return prefix + row.get("id");
    }

    private List<Map<String, Object>> branches(UUID tenantId, Object branchesJson) {
        List<Object> saved = jsonList(branchesJson);
        if (!saved.isEmpty()) {
            return saved.stream().filter(Map.class::isInstance).map(item -> (Map<String, Object>) item).toList();
        }
        return jdbc.queryForList(
                        """
                        SELECT id, name, address, city, latitude, longitude, phone, opening_hours, is_main_branch, status
                        FROM branches
                        WHERE tenant_id = ?
                        ORDER BY is_main_branch DESC NULLS LAST, created_at ASC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id",
                                        "BR-" + row.get("id"),
                                        "name",
                                        row.get("name"),
                                        "address",
                                        row.get("address"),
                                        "city",
                                        row.get("city"),
                                        "latitude",
                                        row.get("latitude"),
                                        "longitude",
                                        row.get("longitude"),
                                        "phone",
                                        row.get("phone"),
                                        "openingHours",
                                        row.get("opening_hours"),
                                        "main",
                                        row.get("is_main_branch"),
                                        "status",
                                        row.get("status")))
                .toList();
    }

    private Map<String, Object> companySummary(UUID tenantId) {
        Map<String, Object> profile = profileResponse(tenantId);
        return mapOf(
                "id", profile.get("id"),
                "name", profile.get("name"),
                "logoUrl", profile.get("logoUrl"),
                "logoText", profile.get("logoText"));
    }

    private List<Map<String, Object>> recentActivity(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT id, title, content, created_at
                        FROM feed_posts
                        WHERE tenant_id = ?
                        ORDER BY created_at DESC NULLS LAST
                        LIMIT 5
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id",
                                        "POST-" + row.get("id"),
                                        "title",
                                        valueOrDefault(row.get("title"), "Publicacion"),
                                        "detail",
                                        valueOrDefault(row.get("content"), "Actividad registrada"),
                                        "time",
                                        row.get("created_at")))
                .toList();
    }

    private List<Map<String, Object>> alerts(long products, long activeProducts, Map<String, Object> profile) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        if (products == 0) {
            alerts.add(
                    mapOf(
                            "id", "sin-productos",
                            "title", "Publica tu primer producto",
                            "detail", "El catalogo empresarial aun no tiene productos.",
                            "priority", "high",
                            "href", "/empresa/publicaciones"));
        }
        if (activeProducts == 0 && products > 0) {
            alerts.add(
                    mapOf(
                            "id", "sin-activos",
                            "title", "Activa productos visibles",
                            "detail", "Hay productos cargados, pero ninguno figura como activo.",
                            "priority", "medium",
                            "href", "/empresa/publicaciones"));
        }
        if (valueOrDefault(profile.get("logoUrl"), "").isBlank()) {
            alerts.add(
                    mapOf(
                            "id", "logo-pendiente",
                            "title", "Agrega el logo de tu empresa",
                            "detail", "El logo mejora reconocimiento en publicaciones y productos.",
                            "priority", "low",
                            "href", "/empresa/perfil"));
        }
        return alerts;
    }

    private List<Map<String, Object>> recommendedActions(
            long products, long activeProducts, Map<String, Object> profile) {
        List<Map<String, Object>> actions = new ArrayList<>();
        actions.add(
                mapOf(
                        "id", "actualizar-perfil",
                        "title", "Completar perfil comercial",
                        "description", "Revisa contactos, horarios y cobertura para reducir dudas del comprador.",
                        "impact", "Mejora conversion",
                        "cta", "Editar perfil",
                        "href", "/empresa/perfil"));
        if (products == 0 || activeProducts == 0) {
            actions.add(
                    mapOf(
                            "id", "publicar-producto",
                            "title", "Publicar producto principal",
                            "description", "Crea una publicacion de producto con imagen, precio y estado activo.",
                            "impact", "Aumenta visibilidad",
                            "cta", "Crear publicacion",
                            "href", "/empresa/publicaciones"));
        }
        return actions;
    }

    private List<Map<String, Object>> reviewsList(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT r.id, r.user_id, r.listing_id, r.rating, r.comment, r.moderation_status,
                               r.created_at, r.company_response, r.company_response_at
                        FROM reviews r
                        WHERE r.tenant_id = ?
                        ORDER BY r.created_at DESC NULLS LAST
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id", "REV-" + row.get("id"),
                                        "user", userSummary((UUID) row.get("user_id")),
                                        "productOrService", listingSummary((UUID) row.get("listing_id")),
                                        "stars", row.get("rating"),
                                        "comment", row.get("comment"),
                                        "status", row.get("moderation_status"),
                                        "createdAt", row.get("created_at"),
                                        "response", row.get("company_response"),
                                        "respondedAt", row.get("company_response_at"),
                                        "wasResponded", row.get("company_response") != null))
                .toList();
    }

    private List<Map<String, Object>> followUpItems(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT id, rating, comment
                        FROM reviews
                        WHERE tenant_id = ? AND company_response IS NULL
                        ORDER BY created_at DESC NULLS LAST
                        LIMIT 5
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id", "REV-" + row.get("id"),
                                        "title", "Responder resena",
                                        "detail", valueOrDefault(row.get("comment"), "Resena sin respuesta"),
                                        "priority", ratingPriority(row.get("rating"))))
                .toList();
    }

    private List<Map<String, Object>> ratingSummary(UUID tenantId) {
        List<Map<String, Object>> summary = new ArrayList<>();
        for (int stars = 5; stars >= 1; stars--) {
            summary.add(
                    mapOf(
                            "stars", stars,
                            "count", count(
                                    "SELECT COUNT(*) FROM reviews WHERE tenant_id = ? AND ROUND(rating) = ?",
                                    tenantId,
                                    stars)));
        }
        return summary;
    }

    private String ratingPriority(Object rating) {
        BigDecimal value = decimalOrDefault(rating, BigDecimal.ZERO);
        return value.compareTo(BigDecimal.valueOf(3)) <= 0 ? "high" : "normal";
    }

    private List<Map<String, Object>> publicationMetrics(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT fp.id, fp.title, fp.post_type,
                               COUNT(DISTINCT c.id) AS comments,
                               COUNT(DISTINCT l.id) FILTER (WHERE l.liked = TRUE) AS likes
                        FROM feed_posts fp
                        LEFT JOIN post_comments c ON c.feed_post_id = fp.id
                        LEFT JOIN company_publication_likes l ON l.feed_post_id = fp.id
                        WHERE fp.tenant_id = ?
                        GROUP BY fp.id
                        ORDER BY fp.created_at DESC NULLS LAST
                        LIMIT 20
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id", "POST-" + row.get("id"),
                                        "title", row.get("title"),
                                        "type", row.get("post_type"),
                                        "likes", numberOrDefault(row.get("likes"), 0),
                                        "comments", numberOrDefault(row.get("comments"), 0),
                                        "interactions", numberOrDefault(row.get("likes"), 0) + numberOrDefault(row.get("comments"), 0)))
                .toList();
    }

    private List<Map<String, Object>> userComments(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT pc.id, pc.user_id, pc.comment_body, pc.created_at, fp.id AS post_id, fp.title
                        FROM post_comments pc
                        JOIN feed_posts fp ON fp.id = pc.feed_post_id
                        WHERE fp.tenant_id = ?
                        ORDER BY pc.created_at DESC NULLS LAST
                        LIMIT 30
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "id", "CMT-" + row.get("id"),
                                        "publicationId", "POST-" + row.get("post_id"),
                                        "publicationTitle", row.get("title"),
                                        "user", userSummary((UUID) row.get("user_id")),
                                        "text", row.get("comment_body"),
                                        "createdAt", row.get("created_at")))
                .toList();
    }

    private List<Map<String, Object>> growthSeries(UUID tenantId) {
        return jdbc.queryForList(
                        """
                        SELECT metric_date, leads_count, tickets_count, conversions_count, revenue_amount
                        FROM tenant_daily_metrics
                        WHERE tenant_id = ?
                        ORDER BY metric_date ASC NULLS LAST
                        LIMIT 30
                        """,
                        tenantId)
                .stream()
                .map(
                        row ->
                                mapOf(
                                        "date", row.get("metric_date"),
                                        "leads", numberOrDefault(row.get("leads_count"), 0),
                                        "tickets", numberOrDefault(row.get("tickets_count"), 0),
                                        "conversions", numberOrDefault(row.get("conversions_count"), 0),
                                        "revenue", decimalOrDefault(row.get("revenue_amount"), BigDecimal.ZERO)))
                .toList();
    }

    private Object latestTimestamp(String sql, Object... args) {
        try {
            return jdbc.queryForObject(sql, Object.class, args);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private Map<String, Object> metric(
            String id, String label, Object value, String trend, String tone, String href) {
        return mapOf("id", id, "label", label, "value", value, "trend", trend, "tone", tone, "href", href);
    }

    private void ensurePublication(UUID tenantId, UUID postId) {
        if (count("SELECT COUNT(*) FROM feed_posts WHERE tenant_id = ? AND id = ?", tenantId, postId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publication not found");
        }
    }

    private Optional<UUID> existingLike(UUID postId, UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT id FROM company_publication_likes WHERE feed_post_id = ? AND user_id = ? LIMIT 1",
                            UUID.class,
                            postId,
                            userId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private boolean likedBy(UUID postId, UUID userId) {
        return count(
                        "SELECT COUNT(*) FROM company_publication_likes WHERE feed_post_id = ? AND user_id = ? AND liked = TRUE",
                        postId,
                        userId)
                > 0;
    }

    private long likesCount(UUID postId) {
        return count(
                "SELECT COUNT(*) FROM company_publication_likes WHERE feed_post_id = ? AND liked = TRUE",
                postId);
    }

    private long commentsCount(UUID postId) {
        return count(
                "SELECT COUNT(*) FROM post_comments WHERE feed_post_id = ? AND LOWER(COALESCE(status, 'active')) IN ('active', 'activo')",
                postId);
    }

    private UUID firstCategoryId() {
        try {
            return jdbc.queryForObject(
                    "SELECT id FROM catalog_categories ORDER BY name ASC LIMIT 1", UUID.class);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private String companyName(UUID tenantId) {
        try {
            return jdbc.queryForObject("SELECT business_name FROM tenants WHERE id = ?", String.class, tenantId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
    }

    private long count(String sql, Object... args) {
        Long value = jdbc.queryForObject(sql, Long.class, args);
        return value == null ? 0L : value;
    }

    private BigDecimal decimalQuery(String sql, Object... args) {
        BigDecimal value = jdbc.queryForObject(sql, BigDecimal.class, args);
        return value == null ? BigDecimal.ZERO : value;
    }

    private UUID parseRequiredUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id or X-Tenant-Id is required");
        }
        return parseUuid(userId, "X-User-Id is invalid");
    }

    private UUID parseOptionalUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseUuid(value, "Identifier is invalid");
    }

    private UUID parsePrefixedUuid(String value, String prefix) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.regionMatches(true, 0, prefix, 0, prefix.length())) {
            normalized = normalized.substring(prefix.length());
        }
        return parseUuid(normalized, "Identifier is invalid");
    }

    private UUID parseUuid(String value, String message) {
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private String jsonValue(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON value");
        }
    }

    private List<Object> jsonList(Object json) {
        if (json == null || json.toString().isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json.toString(), LIST_TYPE);
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }

    private Map<String, Object> jsonMap(Object json) {
        if (json == null || json.toString().isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json.toString(), MAP_TYPE);
        } catch (JsonProcessingException ex) {
            return new LinkedHashMap<>();
        }
    }

    private Map<String, Object> settings(Object json) {
        Map<String, Object> settings = jsonMap(json);
        if (settings.isEmpty()) {
            settings.put("editable", true);
            settings.put("visibility", "public");
        }
        return settings;
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numeric value is invalid");
        }
    }

    private Integer integerValue(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Integer value is invalid");
        }
    }

    private boolean booleanValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private String requestTitle(Map<String, Object> request) {
        String title = stringValue(request.get("title"));
        return title == null ? stringValue(request.get("name")) : title;
    }

    private BigDecimal requestPrice(Map<String, Object> request) {
        BigDecimal currentPrice = decimalValue(request.get("currentPrice"));
        return currentPrice == null ? decimalValue(request.get("price")) : currentPrice;
    }

    private String listingTypeForPublication(String type) {
        return switch (type) {
            case "service" -> "SERVICE";
            case "offer" -> "OFFER";
            default -> "PRODUCT";
        };
    }

    private String valueOrDefault(Object value, Object fallback) {
        if (value == null || value.toString().isBlank()) {
            return fallback == null ? "" : fallback.toString();
        }
        return value.toString();
    }

    private BigDecimal decimalOrDefault(Object value, BigDecimal fallback) {
        BigDecimal parsed = decimalValue(value);
        return parsed == null ? fallback : parsed;
    }

    private int numberOrDefault(Object value, int fallback) {
        Integer parsed = integerValue(value);
        return parsed == null ? fallback : parsed;
    }

    private int boundedScore(long value, int target) {
        if (target <= 0) {
            return 0;
        }
        return (int) Math.min(100, Math.round((value * 100.0) / target));
    }

    private int profileCompletion(Map<String, Object> profile) {
        int total = 7;
        int done = 0;
        done += valueOrDefault(profile.get("name"), "").isBlank() ? 0 : 1;
        done += valueOrDefault(profile.get("logoUrl"), "").isBlank() ? 0 : 1;
        done += valueOrDefault(profile.get("description"), "").isBlank() ? 0 : 1;
        done += valueOrDefault(profile.get("about"), "").isBlank() ? 0 : 1;
        done += ((List<?>) profile.get("contacts")).isEmpty() ? 0 : 1;
        done += ((List<?>) profile.get("schedules")).isEmpty() ? 0 : 1;
        done += ((List<?>) profile.get("branches")).isEmpty() ? 0 : 1;
        return (int) Math.round((done * 100.0) / total);
    }

    private String tagFor(Map<String, Object> row) {
        String type = valueOrDefault(row.get("post_type"), "post").toLowerCase(Locale.ROOT);
        if ("product".equals(type)) {
            return valueOrDefault(row.get("label"), "Producto");
        }
        return valueOrDefault(row.get("label"), "Publicacion");
    }

    private String initials(Object name) {
        String text = valueOrDefault(name, "Empresa");
        String[] parts = text.trim().split("\\s+");
        String first = parts.length > 0 && !parts[0].isBlank() ? parts[0].substring(0, 1) : "E";
        String second = parts.length > 1 && !parts[1].isBlank() ? parts[1].substring(0, 1) : "";
        return (first + second).toUpperCase(Locale.ROOT);
    }

    private Map<String, Object> mapOf(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            map.put(values[index].toString(), values[index + 1]);
        }
        return map;
    }
}
