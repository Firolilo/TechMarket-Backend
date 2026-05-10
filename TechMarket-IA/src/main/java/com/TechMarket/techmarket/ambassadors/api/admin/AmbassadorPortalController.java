package com.techmarket.techmarket.ambassadors.api.admin;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionDisputeJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorInvitationJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorLeadJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOnboardingMilestoneJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOnboardingReminderJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOnboardingTaskJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorPayoutMethodJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralActivityJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralFileJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralLinkJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralNoteJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorWithdrawalJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionDisputeSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorInvitationSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorLeadSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOnboardingMilestoneSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOnboardingReminderSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOnboardingTaskSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorPayoutMethodSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralActivitySpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralFileSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralLinkSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralNoteSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorWithdrawalSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatReadReceiptJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatReadReceiptSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ambassadors")
public class AmbassadorPortalController {

    private static final String AMBASSADOR_CHAT_TYPE = "AMBASSADOR_CHAT";

    private final AmbassadorSpringDataRepository ambassadorRepository;
    private final AmbassadorReferralSpringDataRepository referralRepository;
    private final AmbassadorCommissionSpringDataRepository commissionRepository;
    private final AmbassadorReferralLinkSpringDataRepository referralLinkRepository;
    private final AmbassadorReferralActivitySpringDataRepository activityRepository;
    private final AmbassadorReferralNoteSpringDataRepository noteRepository;
    private final AmbassadorReferralFileSpringDataRepository fileRepository;
    private final AmbassadorOnboardingTaskSpringDataRepository onboardingTaskRepository;
    private final AmbassadorOnboardingReminderSpringDataRepository reminderRepository;
    private final AmbassadorOnboardingMilestoneSpringDataRepository milestoneRepository;
    private final AmbassadorLeadSpringDataRepository leadRepository;
    private final AmbassadorCommissionDisputeSpringDataRepository commissionDisputeRepository;
    private final AmbassadorWithdrawalSpringDataRepository withdrawalRepository;
    private final AmbassadorPayoutMethodSpringDataRepository payoutMethodRepository;
    private final AmbassadorInvitationSpringDataRepository invitationRepository;
    private final ClientChatSpringDataRepository chatRepository;
    private final ClientChatMessageSpringDataRepository chatMessageRepository;
    private final ClientChatReadReceiptSpringDataRepository readReceiptRepository;
    private final UserSpringDataRepository userRepository;
    private final TenantSpringDataRepository tenantRepository;

    public AmbassadorPortalController(
            AmbassadorSpringDataRepository ambassadorRepository,
            AmbassadorReferralSpringDataRepository referralRepository,
            AmbassadorCommissionSpringDataRepository commissionRepository,
            AmbassadorReferralLinkSpringDataRepository referralLinkRepository,
            AmbassadorReferralActivitySpringDataRepository activityRepository,
            AmbassadorReferralNoteSpringDataRepository noteRepository,
            AmbassadorReferralFileSpringDataRepository fileRepository,
            AmbassadorOnboardingTaskSpringDataRepository onboardingTaskRepository,
            AmbassadorOnboardingReminderSpringDataRepository reminderRepository,
            AmbassadorOnboardingMilestoneSpringDataRepository milestoneRepository,
            AmbassadorLeadSpringDataRepository leadRepository,
            AmbassadorCommissionDisputeSpringDataRepository commissionDisputeRepository,
            AmbassadorWithdrawalSpringDataRepository withdrawalRepository,
            AmbassadorPayoutMethodSpringDataRepository payoutMethodRepository,
            AmbassadorInvitationSpringDataRepository invitationRepository,
            ClientChatSpringDataRepository chatRepository,
            ClientChatMessageSpringDataRepository chatMessageRepository,
            ClientChatReadReceiptSpringDataRepository readReceiptRepository,
            UserSpringDataRepository userRepository,
            TenantSpringDataRepository tenantRepository) {
        this.ambassadorRepository = ambassadorRepository;
        this.referralRepository = referralRepository;
        this.commissionRepository = commissionRepository;
        this.referralLinkRepository = referralLinkRepository;
        this.activityRepository = activityRepository;
        this.noteRepository = noteRepository;
        this.fileRepository = fileRepository;
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.reminderRepository = reminderRepository;
        this.milestoneRepository = milestoneRepository;
        this.leadRepository = leadRepository;
        this.commissionDisputeRepository = commissionDisputeRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.payoutMethodRepository = payoutMethodRepository;
        this.invitationRepository = invitationRepository;
        this.chatRepository = chatRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.readReceiptRepository = readReceiptRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/profile")
    public AmbassadorProfileResponse profile(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UserJpaEntity user = findUser(parseUserId(userId));
        AmbassadorJpaEntity ambassador = resolveAmbassador(user);
        return toProfileResponse(ambassador, user);
    }

    @PutMapping("/profile")
    @Transactional
    public MessageResponse updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateAmbassadorProfileRequest request) {
        UserJpaEntity user = findUser(parseUserId(userId));
        AmbassadorJpaEntity ambassador = resolveAmbassador(user);
        user.setFirstName(request.nombre());
        user.setLastName(request.apellido());
        user.setPhone(request.telefono());
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
        ambassador.setCity(request.ciudad());
        ambassador.setDescription(request.descripcion());
        ambassadorRepository.save(ambassador);
        return new MessageResponse("Perfil actualizado correctamente");
    }

    @PostMapping("/profile/photo")
    public PhotoResponse updatePhoto(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdatePhotoRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        ambassador.setAvatarUrl(request.url());
        ambassadorRepository.save(ambassador);
        return new PhotoResponse(ambassador.getAvatarUrl());
    }

    @GetMapping("/profile/stats")
    public AmbassadorStatsResponse stats(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        long referrals = referralRepository.countByAmbassadorId(ambassador.getId());
        long active = referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassador.getId(), "activo");
        double conversionRate =
                referrals == 0 ? 0 : BigDecimal.valueOf(active * 100.0 / referrals).setScale(1, RoundingMode.HALF_UP).doubleValue();
        return new AmbassadorStatsResponse(
                referrals,
                active,
                conversionRate,
                "Bs " + commissionRepository.sumAmountByAmbassadorId(ambassador.getId()),
                valueOrDefault(ambassador.getLevel(), "Bronze"));
    }

    @GetMapping("/settings")
    public AmbassadorSettingsResponse settings(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return new AmbassadorSettingsResponse(
                ambassador.isEmailNotifications(),
                ambassador.isPushNotifications(),
                ambassador.isPublicProfile(),
                valueOrDefault(ambassador.getLanguage(), "es"));
    }

    @PutMapping("/settings")
    public MessageResponse updateSettings(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateAmbassadorSettingsRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        ambassador.setEmailNotifications(request.notificacionesEmail());
        ambassador.setPushNotifications(request.notificacionesPush());
        ambassador.setPublicProfile(request.mostrarPerfilPublico());
        ambassadorRepository.save(ambassador);
        return new MessageResponse("Configuración actualizada");
    }

    @GetMapping("/referral-links")
    public List<ReferralLinkResponse> referralLinks(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return referralLinkRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).stream()
                .map(this::toReferralLinkResponse)
                .toList();
    }

    @PostMapping("/referral-links")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReferralLinkResponse createReferralLink(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateReferralLinkRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        String code = buildReferralCode(ambassador, request.ciudad(), request.segmento());
        AmbassadorReferralLinkJpaEntity link = new AmbassadorReferralLinkJpaEntity();
        link.setId(UUID.randomUUID());
        link.setAmbassadorId(ambassador.getId());
        link.setName(request.nombre());
        link.setSegment(request.segmento());
        link.setCity(request.ciudad());
        link.setCode(code);
        link.setUrl(referralUrl(code));
        link.setActive(true);
        link.setCreatedAt(OffsetDateTime.now());
        link.setUpdatedAt(link.getCreatedAt());
        AmbassadorReferralLinkJpaEntity saved = referralLinkRepository.save(link);
        return new CreateReferralLinkResponse(formatReferralLinkId(saved.getId()), saved.getCode(), saved.getUrl());
    }

    @GetMapping("/referral-links/{linkId}")
    public ReferralLinkDetailResponse referralLink(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String linkId) {
        return toReferralLinkDetailResponse(findReferralLink(linkId, resolveAmbassador(findUser(parseUserId(userId))).getId()));
    }

    @PutMapping("/referral-links/{linkId}")
    public MessageResponse updateReferralLink(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String linkId,
            @Valid @RequestBody UpdateReferralLinkRequest request) {
        AmbassadorReferralLinkJpaEntity link =
                findReferralLink(linkId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        link.setName(request.nombre());
        link.setSegment(request.segmento());
        link.setUpdatedAt(OffsetDateTime.now());
        referralLinkRepository.save(link);
        return new MessageResponse("Link actualizado correctamente");
    }

    @PatchMapping("/referral-links/{linkId}/status")
    public ReferralLinkStatusResponse updateReferralLinkStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String linkId,
            @Valid @RequestBody UpdateReferralLinkStatusRequest request) {
        AmbassadorReferralLinkJpaEntity link =
                findReferralLink(linkId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        link.setActive(request.activo());
        link.setUpdatedAt(OffsetDateTime.now());
        AmbassadorReferralLinkJpaEntity saved = referralLinkRepository.save(link);
        return new ReferralLinkStatusResponse(formatReferralLinkId(saved.getId()), saved.isActive());
    }

    @DeleteMapping("/referral-links/{linkId}")
    public MessageResponse deleteReferralLink(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String linkId) {
        referralLinkRepository.delete(
                findReferralLink(linkId, resolveAmbassador(findUser(parseUserId(userId))).getId()));
        return new MessageResponse("Link de referido eliminado");
    }

    @GetMapping("/referral-links/{linkId}/qr")
    public ReferralLinkQrResponse referralLinkQr(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String linkId) {
        AmbassadorReferralLinkJpaEntity link =
                findReferralLink(linkId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return new ReferralLinkQrResponse("https://cdn.techmarket.bo/qr/" + link.getCode() + ".png");
    }

    @GetMapping("/referral-codes")
    public List<ReferralCodeResponse> referralCodes(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        List<ReferralCodeResponse> links =
                referralLinkRepository.findAllByAmbassadorIdAndActiveTrueOrderByCreatedAtDesc(ambassador.getId()).stream()
                        .map(link -> new ReferralCodeResponse(link.getCode(), valueOrDefault(link.getSegment(), "campaña"), link.getConversions(), true))
                        .toList();
        if (!links.isEmpty()) {
            return links;
        }
        return List.of(new ReferralCodeResponse(ambassador.getReferralCode(), "general", referralRepository.countByAmbassadorId(ambassador.getId()), true));
    }

    @GetMapping("/referrals")
    public List<AmbassadorReferralListResponse> referrals(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return referralRepository.findByAmbassadorId(ambassador.getId()).stream()
                .map(this::toReferralListResponse)
                .toList();
    }

    @PostMapping("/referrals")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReferralResponse createReferral(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateReferralRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        OffsetDateTime now = OffsetDateTime.now();
        AmbassadorReferralJpaEntity referral = new AmbassadorReferralJpaEntity();
        referral.setId(UUID.randomUUID());
        referral.setAmbassadorId(ambassador.getId());
        referral.setName(request.nombre());
        referral.setReferralType(request.tipo());
        referral.setContactName(request.contacto());
        referral.setPhone(request.telefono());
        referral.setEmail(request.email());
        referral.setCity(request.ciudad());
        referral.setAttributionChannel("manual");
        referral.setUsedCode(ambassador.getReferralCode());
        referral.setStatus("prospecto");
        referral.setCreatedAt(now);
        referral.setLastActivityAt(now);
        AmbassadorReferralJpaEntity saved = referralRepository.save(referral);
        createActivity(saved.getId(), "registro", "Prospecto registrado manualmente", now);
        return new CreateReferralResponse(
                formatBusinessId(saved.getId()), saved.getStatus(), "Prospecto registrado correctamente");
    }

    @GetMapping("/referrals/{referralId}")
    public AmbassadorReferralDetailResponse referral(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return toReferralDetailResponse(referral);
    }

    @PutMapping("/referrals/{referralId}")
    public MessageResponse updateReferral(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId,
            @Valid @RequestBody UpdateReferralRequest request) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        referral.setContactName(request.contacto());
        referral.setPhone(request.telefono());
        referral.setCity(request.ciudad());
        referral.setLastActivityAt(OffsetDateTime.now());
        referralRepository.save(referral);
        createActivity(referral.getId(), "actualizacion", "Datos del referido actualizados", referral.getLastActivityAt());
        return new MessageResponse("Referido actualizado correctamente");
    }

    @PatchMapping("/referrals/{referralId}/status")
    public ReferralStatusResponse updateReferralStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId,
            @Valid @RequestBody UpdateReferralStatusRequest request) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        referral.setStatus(request.estado());
        referral.setLastActivityAt(OffsetDateTime.now());
        AmbassadorReferralJpaEntity saved = referralRepository.save(referral);
        createActivity(saved.getId(), "estado", "Estado actualizado a " + saved.getStatus(), saved.getLastActivityAt());
        return new ReferralStatusResponse(formatBusinessId(saved.getId()), saved.getStatus());
    }

    @DeleteMapping("/referrals/{referralId}")
    public MessageResponse deleteReferral(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        if (!"prospecto".equalsIgnoreCase(valueOrDefault(referral.getStatus(), ""))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only prospect referrals can be deleted");
        }
        referralRepository.delete(referral);
        return new MessageResponse("Referido eliminado correctamente");
    }

    @GetMapping("/referrals/{referralId}/activity")
    public List<ReferralActivityResponse> referralActivity(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return activityRepository.findAllByAmbassadorReferralIdOrderByCreatedAtDesc(referral.getId()).stream()
                .map(activity -> new ReferralActivityResponse("ACT-" + activity.getId(), activity.getActivityType(), activity.getDescription(), activity.getCreatedAt()))
                .toList();
    }

    @PostMapping("/referrals/{referralId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReferralNoteResponse createReferralNote(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId,
            @Valid @RequestBody CreateReferralNoteRequest request) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        AmbassadorReferralNoteJpaEntity note = new AmbassadorReferralNoteJpaEntity();
        note.setId(UUID.randomUUID());
        note.setAmbassadorReferralId(referral.getId());
        note.setNote(request.nota());
        note.setCreatedAt(OffsetDateTime.now());
        AmbassadorReferralNoteJpaEntity saved = noteRepository.save(note);
        createActivity(referral.getId(), "nota", "Nota interna agregada", saved.getCreatedAt());
        return new CreateReferralNoteResponse("NOTE-" + saved.getId(), "Nota agregada");
    }

    @GetMapping("/referrals/{referralId}/notes")
    public List<ReferralNoteResponse> referralNotes(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return noteRepository.findAllByAmbassadorReferralIdOrderByCreatedAtDesc(referral.getId()).stream()
                .map(note -> new ReferralNoteResponse("NOTE-" + note.getId(), note.getNote(), note.getCreatedAt()))
                .toList();
    }

    @PostMapping("/referrals/{referralId}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ReferralFileResponse createReferralFile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String referralId,
            @Valid @RequestBody CreateReferralFileRequest request) {
        AmbassadorReferralJpaEntity referral = findReferral(referralId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        AmbassadorReferralFileJpaEntity file = new AmbassadorReferralFileJpaEntity();
        file.setId(UUID.randomUUID());
        file.setAmbassadorReferralId(referral.getId());
        file.setUrl(request.url());
        file.setCreatedAt(OffsetDateTime.now());
        AmbassadorReferralFileJpaEntity saved = fileRepository.save(file);
        createActivity(referral.getId(), "archivo", "Archivo asociado al referido", saved.getCreatedAt());
        return new ReferralFileResponse("FILE-" + saved.getId(), saved.getUrl());
    }

    @GetMapping("/onboarding")
    public List<OnboardingSummaryResponse> onboarding(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return referralRepository.findByAmbassadorId(ambassador.getId()).stream()
                .filter(referral -> !"activo".equalsIgnoreCase(valueOrDefault(referral.getStatus(), "")))
                .map(referral -> new OnboardingSummaryResponse(formatOnboardingId(referral.getId()), formatBusinessId(referral.getId()), referralName(referral), onboardingProgress(referral.getId()), valueOrDefault(referral.getStatus(), "en_proceso")))
                .toList();
    }

    @GetMapping("/onboarding/{onboardingId}")
    public OnboardingDetailResponse onboardingDetail(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String onboardingId) {
        AmbassadorReferralJpaEntity referral = findReferralByOnboarding(onboardingId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return new OnboardingDetailResponse(formatOnboardingId(referral.getId()), referralName(referral), valueOrDefault(referral.getStatus(), "en_proceso"), onboardingProgress(referral.getId()), defaultOnboardingSteps(referral.getId()));
    }

    @PostMapping("/onboarding/{onboardingId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOnboardingTaskResponse createOnboardingTask(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String onboardingId,
            @Valid @RequestBody CreateOnboardingTaskRequest request) {
        AmbassadorReferralJpaEntity referral = findReferralByOnboarding(onboardingId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        AmbassadorOnboardingTaskJpaEntity task = new AmbassadorOnboardingTaskJpaEntity();
        task.setId(UUID.randomUUID());
        task.setAmbassadorReferralId(referral.getId());
        task.setTitle(request.titulo());
        task.setDueDate(request.fechaLimite());
        task.setStatus("pendiente");
        task.setCreatedAt(OffsetDateTime.now());
        AmbassadorOnboardingTaskJpaEntity saved = onboardingTaskRepository.save(task);
        return new CreateOnboardingTaskResponse("TASK-" + saved.getId(), "Tarea creada");
    }

    @GetMapping("/onboarding/{onboardingId}/tasks")
    public List<OnboardingTaskResponse> onboardingTasks(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String onboardingId) {
        AmbassadorReferralJpaEntity referral = findReferralByOnboarding(onboardingId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return onboardingTaskRepository.findAllByAmbassadorReferralIdOrderByCreatedAtDesc(referral.getId()).stream()
                .map(task -> new OnboardingTaskResponse("TASK-" + task.getId(), task.getTitle(), task.getStatus(), task.getDueDate()))
                .toList();
    }

    @PatchMapping("/onboarding/tasks/{taskId}/status")
    public OnboardingTaskStatusResponse updateOnboardingTaskStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        UUID taskUuid = parsePrefixedUuid(taskId, "TASK-");
        AmbassadorOnboardingTaskJpaEntity task = onboardingTaskRepository.findById(taskUuid)
                .filter(candidate -> referralRepository.findByIdAndAmbassadorId(candidate.getAmbassadorReferralId(), ambassador.getId()).isPresent())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setStatus(request.estado());
        task.setCompletedAt("completada".equalsIgnoreCase(request.estado()) ? OffsetDateTime.now() : null);
        AmbassadorOnboardingTaskJpaEntity saved = onboardingTaskRepository.save(task);
        return new OnboardingTaskStatusResponse("TASK-" + saved.getId(), saved.getStatus());
    }

    @PostMapping("/onboarding/{onboardingId}/reminders")
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse createReminder(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String onboardingId,
            @Valid @RequestBody CreateReminderRequest request) {
        AmbassadorReferralJpaEntity referral = findReferralByOnboarding(onboardingId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        AmbassadorOnboardingReminderJpaEntity reminder = new AmbassadorOnboardingReminderJpaEntity();
        reminder.setId(UUID.randomUUID());
        reminder.setAmbassadorReferralId(referral.getId());
        reminder.setReminderAt(request.fecha());
        reminder.setMessage(request.mensaje());
        reminder.setCreatedAt(OffsetDateTime.now());
        AmbassadorOnboardingReminderJpaEntity saved = reminderRepository.save(reminder);
        return new ReminderResponse("REM-" + saved.getId(), "Recordatorio creado");
    }

    @GetMapping("/onboarding/milestones")
    public List<MilestoneResponse> milestones() {
        return defaultMilestones();
    }

    @PatchMapping("/onboarding/{onboardingId}/milestones/{milestoneId}")
    public MessageResponse completeMilestone(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String onboardingId,
            @PathVariable String milestoneId) {
        AmbassadorReferralJpaEntity referral = findReferralByOnboarding(onboardingId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        AmbassadorOnboardingMilestoneJpaEntity milestone = milestoneRepository.findByAmbassadorReferralIdAndMilestoneCode(referral.getId(), milestoneId)
                .orElseGet(() -> {
                    AmbassadorOnboardingMilestoneJpaEntity created = new AmbassadorOnboardingMilestoneJpaEntity();
                    created.setId(UUID.randomUUID());
                    created.setAmbassadorReferralId(referral.getId());
                    created.setMilestoneCode(milestoneId);
                    return created;
                });
        milestone.setCompleted(true);
        milestone.setCompletedAt(OffsetDateTime.now());
        milestoneRepository.save(milestone);
        return new MessageResponse("Hito marcado como completado");
    }

    @GetMapping("/leads")
    public List<LeadSummaryResponse> leads(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return leadRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).stream()
                .map(lead -> new LeadSummaryResponse(formatLeadId(lead.getId()), lead.getName(), lead.getLeadType(), lead.getStatus(), lead.getSource()))
                .toList();
    }

    @PostMapping("/leads")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateLeadResponse createLead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateLeadRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        OffsetDateTime now = OffsetDateTime.now();
        AmbassadorLeadJpaEntity lead = new AmbassadorLeadJpaEntity();
        lead.setId(UUID.randomUUID());
        lead.setAmbassadorId(ambassador.getId());
        lead.setName(request.nombre());
        lead.setLeadType(request.tipo());
        lead.setContactName(request.contacto());
        lead.setPhone(request.telefono());
        lead.setSource(request.fuente());
        lead.setStatus("nuevo");
        lead.setCloseProbability(68);
        lead.setCreatedAt(now);
        lead.setUpdatedAt(now);
        AmbassadorLeadJpaEntity saved = leadRepository.save(lead);
        return new CreateLeadResponse(formatLeadId(saved.getId()), "Lead creado correctamente");
    }

    @GetMapping("/leads/{leadId}")
    public LeadDetailResponse lead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String leadId) {
        AmbassadorLeadJpaEntity lead = findLead(leadId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return new LeadDetailResponse(formatLeadId(lead.getId()), lead.getName(), lead.getLeadType(), lead.getStatus(), lead.getCloseProbability());
    }

    @PutMapping("/leads/{leadId}")
    public MessageResponse updateLead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String leadId,
            @Valid @RequestBody UpdateLeadRequest request) {
        AmbassadorLeadJpaEntity lead = findLead(leadId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        lead.setName(request.nombre());
        lead.setLeadType(request.tipo());
        lead.setContactName(request.contacto());
        lead.setPhone(request.telefono());
        lead.setSource(request.fuente());
        lead.setUpdatedAt(OffsetDateTime.now());
        leadRepository.save(lead);
        return new MessageResponse("Lead actualizado");
    }

    @PatchMapping("/leads/{leadId}/status")
    public LeadStatusResponse updateLeadStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String leadId,
            @Valid @RequestBody UpdateLeadStatusRequest request) {
        AmbassadorLeadJpaEntity lead = findLead(leadId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        lead.setStatus(request.estado());
        lead.setUpdatedAt(OffsetDateTime.now());
        AmbassadorLeadJpaEntity saved = leadRepository.save(lead);
        return new LeadStatusResponse(formatLeadId(saved.getId()), saved.getStatus());
    }

    @PostMapping("/leads/{leadId}/convert")
    @ResponseStatus(HttpStatus.CREATED)
    public ConvertLeadResponse convertLead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String leadId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorLeadJpaEntity lead = findLead(leadId, ambassador.getId());
        OffsetDateTime now = OffsetDateTime.now();
        AmbassadorReferralJpaEntity referral = new AmbassadorReferralJpaEntity();
        referral.setId(UUID.randomUUID());
        referral.setAmbassadorId(ambassador.getId());
        referral.setName(lead.getName());
        referral.setReferralType(lead.getLeadType());
        referral.setContactName(lead.getContactName());
        referral.setPhone(lead.getPhone());
        referral.setAttributionChannel(valueOrDefault(lead.getSource(), "lead"));
        referral.setUsedCode(ambassador.getReferralCode());
        referral.setStatus("prospecto");
        referral.setCreatedAt(now);
        referral.setLastActivityAt(now);
        AmbassadorReferralJpaEntity saved = referralRepository.save(referral);
        lead.setStatus("convertido");
        lead.setUpdatedAt(now);
        leadRepository.save(lead);
        createActivity(saved.getId(), "conversion", "Lead convertido en referido", now);
        return new ConvertLeadResponse(formatBusinessId(saved.getId()), "Lead convertido en referido");
    }

    @DeleteMapping("/leads/{leadId}")
    public MessageResponse deleteLead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String leadId) {
        leadRepository.delete(findLead(leadId, resolveAmbassador(findUser(parseUserId(userId))).getId()));
        return new MessageResponse("Lead eliminado");
    }

    @GetMapping("/commissions")
    public List<CommissionResponse> commissions(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return commissionRepository.findByAmbassadorId(ambassador.getId()).stream()
                .map(this::toCommissionResponse)
                .toList();
    }

    @GetMapping("/commissions/summary")
    public CommissionSummaryResponse commissionSummary(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        List<AmbassadorCommissionJpaEntity> commissions = commissionRepository.findByAmbassadorId(ambassador.getId());
        return new CommissionSummaryResponse(
                formatMoney(sumCommissions(commissions, null)),
                formatMoney(sumCommissions(commissions, "disponible")),
                formatMoney(sumCommissions(commissions, "pendiente")),
                formatMoney(sumCommissions(commissions, "pagado")));
    }

    @GetMapping("/commissions/{commissionId}")
    public CommissionDetailResponse commission(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String commissionId) {
        AmbassadorCommissionJpaEntity commission =
                findCommission(commissionId, resolveAmbassador(findUser(parseUserId(userId))).getId());
        return new CommissionDetailResponse(
                formatCommissionId(commission.getId()),
                referralNameById(commission.getAmbassadorReferralId()),
                conceptFor(commission),
                formatMoney(parseAmount(commission.getAmount())),
                5,
                commission.getStatus(),
                commission.getGeneratedAt());
    }

    @PostMapping("/commissions/{commissionId}/dispute")
    @ResponseStatus(HttpStatus.CREATED)
    public CommissionDisputeResponse disputeCommission(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String commissionId,
            @Valid @RequestBody CommissionDisputeRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorCommissionJpaEntity commission = findCommission(commissionId, ambassador.getId());
        AmbassadorCommissionDisputeJpaEntity dispute = new AmbassadorCommissionDisputeJpaEntity();
        dispute.setId(UUID.randomUUID());
        dispute.setAmbassadorId(ambassador.getId());
        dispute.setAmbassadorCommissionId(commission.getId());
        dispute.setReason(request.motivo());
        dispute.setDescription(request.descripcion());
        dispute.setStatus("pendiente_revision");
        dispute.setCreatedAt(OffsetDateTime.now());
        AmbassadorCommissionDisputeJpaEntity saved = commissionDisputeRepository.save(dispute);
        return new CommissionDisputeResponse("DSP-" + saved.getId(), saved.getStatus());
    }

    @GetMapping("/wallet")
    public AmbassadorWalletResponse wallet(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        List<AmbassadorCommissionJpaEntity> commissions = commissionRepository.findByAmbassadorId(ambassador.getId());
        return new AmbassadorWalletResponse(
                formatMoney(sumCommissions(commissions, "disponible")),
                formatMoney(sumCommissions(commissions, "pendiente")),
                formatMoney(sumWithdrawals(ambassador.getId(), "pagado")));
    }

    @PostMapping("/wallet/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public WithdrawalResponse withdraw(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody WithdrawalRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorWithdrawalJpaEntity withdrawal = new AmbassadorWithdrawalJpaEntity();
        withdrawal.setId(UUID.randomUUID());
        withdrawal.setAmbassadorId(ambassador.getId());
        withdrawal.setAmount(request.monto());
        withdrawal.setPayoutMethodId(request.metodoPagoId());
        withdrawal.setStatus("pendiente");
        withdrawal.setRequestedAt(OffsetDateTime.now());
        withdrawal.setEstimatedAt(LocalDate.now().plusDays(4));
        AmbassadorWithdrawalJpaEntity saved = withdrawalRepository.save(withdrawal);
        return new WithdrawalResponse("WDR-" + saved.getId(), saved.getStatus(), saved.getEstimatedAt());
    }

    @GetMapping("/payouts")
    public List<PayoutResponse> payouts(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return withdrawalRepository.findAllByAmbassadorIdOrderByRequestedAtDesc(ambassador.getId()).stream()
                .map(
                        withdrawal ->
                                new PayoutResponse(
                                        formatWithdrawalId(withdrawal.getId()),
                                        formatMoney(parseAmount(withdrawal.getAmount())),
                                        withdrawal.getStatus(),
                                        withdrawal.getRequestedAt() == null
                                                ? null
                                                : withdrawal.getRequestedAt().toLocalDate().toString()))
                .toList();
    }

    @GetMapping("/payout-methods")
    public List<PayoutMethodResponse> payoutMethods(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return payoutMethodRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).stream()
                .map(
                        method ->
                                new PayoutMethodResponse(
                                        formatPayoutMethodId(method.getId()),
                                        method.getMethodType(),
                                        method.getBank(),
                                        method.getLast4(),
                                        method.isDefaultMethod()))
                .toList();
    }

    @PostMapping("/payout-methods")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePayoutMethodResponse createPayoutMethod(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreatePayoutMethodRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorPayoutMethodJpaEntity method = new AmbassadorPayoutMethodJpaEntity();
        method.setId(UUID.randomUUID());
        method.setAmbassadorId(ambassador.getId());
        method.setMethodType(request.tipo());
        method.setBank(request.banco());
        method.setAccountNumber(request.numeroCuenta());
        method.setHolderName(request.titular());
        method.setLast4(last4(request.numeroCuenta()));
        method.setDefaultMethod(!payoutMethodRepository.existsByAmbassadorId(ambassador.getId()));
        method.setCreatedAt(OffsetDateTime.now());
        AmbassadorPayoutMethodJpaEntity saved = payoutMethodRepository.save(method);
        return new CreatePayoutMethodResponse(formatPayoutMethodId(saved.getId()), "Método de pago agregado");
    }

    @DeleteMapping("/payout-methods/{methodId}")
    public MessageResponse deletePayoutMethod(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String methodId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorPayoutMethodJpaEntity method =
                payoutMethodRepository
                        .findByIdAndAmbassadorId(parsePrefixedUuid(methodId, "PAYM-"), ambassador.getId())
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payout method not found"));
        payoutMethodRepository.delete(method);
        return new MessageResponse("Método de pago eliminado");
    }

    @GetMapping("/network")
    public List<AmbassadorNetworkResponse> network(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return ambassadorRepository.findAllBySponsorAmbassadorId(ambassador.getId()).stream()
                .map(this::toNetworkResponse)
                .toList();
    }

    @GetMapping("/network/tree")
    public AmbassadorNetworkTreeResponse networkTree(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UserJpaEntity user = findUser(parseUserId(userId));
        AmbassadorJpaEntity ambassador = resolveAmbassador(user);
        List<AmbassadorNetworkNodeResponse> children =
                ambassadorRepository.findAllBySponsorAmbassadorId(ambassador.getId()).stream()
                        .map(this::toNetworkNodeResponse)
                        .toList();
        return new AmbassadorNetworkTreeResponse(
                formatAmbassadorId(ambassador.getId()),
                fullName(user),
                valueOrDefault(ambassador.getLevel(), "Bronze"),
                children);
    }

    @PostMapping("/network/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAmbassadorInvitationResponse createInvitation(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateAmbassadorInvitationRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorInvitationJpaEntity invitation = new AmbassadorInvitationJpaEntity();
        invitation.setId(UUID.randomUUID());
        invitation.setAmbassadorId(ambassador.getId());
        invitation.setEmail(request.email());
        invitation.setName(request.nombre());
        invitation.setPhone(request.telefono());
        invitation.setStatus("enviada");
        invitation.setCreatedAt(OffsetDateTime.now());
        AmbassadorInvitationJpaEntity saved = invitationRepository.save(invitation);
        return new CreateAmbassadorInvitationResponse(formatAmbassadorInvitationId(saved.getId()), saved.getStatus());
    }

    @GetMapping("/network/invitations")
    public List<AmbassadorInvitationResponse> invitations(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return invitationRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).stream()
                .map(
                        invitation ->
                                new AmbassadorInvitationResponse(
                                        formatAmbassadorInvitationId(invitation.getId()),
                                        invitation.getEmail(),
                                        invitation.getStatus()))
                .toList();
    }

    @DeleteMapping("/network/invitations/{invitationId}")
    public MessageResponse deleteInvitation(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String invitationId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorInvitationJpaEntity invitation =
                invitationRepository
                        .findByIdAndAmbassadorId(parsePrefixedUuid(invitationId, "INV-AMB-"), ambassador.getId())
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
        invitationRepository.delete(invitation);
        return new MessageResponse("Invitación cancelada");
    }

    @GetMapping("/network/ranking")
    public List<AmbassadorRankingResponse> ranking() {
        int[] position = {0};
        return ambassadorRepository.findAll().stream()
                .sorted(Comparator.comparing(
                                (AmbassadorJpaEntity ambassador) ->
                                        commissionRepository.sumAmountByAmbassadorId(ambassador.getId()))
                        .reversed())
                .map(ambassador -> new AmbassadorRankingResponse(
                        ++position[0],
                        formatAmbassadorId(ambassador.getId()),
                        ambassadorUser(ambassador).map(this::fullName).orElse("Embajador"),
                        referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassador.getId(), "activo"),
                        formatMoney(commissionRepository.sumAmountByAmbassadorId(ambassador.getId()))))
                .toList();
    }

    @GetMapping("/chats")
    public List<AmbassadorChatSummaryResponse> ambassadorChats(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        resolveAmbassador(findUser(currentUserId));
        return chatRepository
                .findAllByCustomerUserIdAndTicketTypeOrderByCreatedAtDesc(currentUserId, AMBASSADOR_CHAT_TYPE)
                .stream()
                .map(chat -> toAmbassadorChatSummary(chat, currentUserId))
                .toList();
    }

    @PostMapping("/chats")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAmbassadorChatResponse createAmbassadorChat(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateAmbassadorChatRequest request) {
        UserJpaEntity user = findUser(parseUserId(userId));
        AmbassadorJpaEntity ambassador = resolveAmbassador(user);
        AmbassadorReferralJpaEntity referral = findReferral(request.participanteId(), ambassador.getId());
        OffsetDateTime now = OffsetDateTime.now();
        ClientChatJpaEntity chat = new ClientChatJpaEntity();
        chat.setId(UUID.randomUUID());
        chat.setTicketCode(formatAmbassadorChatId(chat.getId()));
        chat.setCustomerUserId(user.getId());
        chat.setTenantId(referral.getTenantId());
        chat.setTicketType(AMBASSADOR_CHAT_TYPE);
        chat.setSubject(referralName(referral));
        chat.setStatus("Abierto");
        chat.setOpenedAt(now);
        chat.setCreatedAt(now);
        ClientChatJpaEntity savedChat = chatRepository.save(chat);

        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(savedChat.getId());
        message.setAuthorUserId(user.getId());
        message.setMessageBody(request.mensajeInicial());
        message.setMessageType("TEXT");
        message.setVisibleToCustomer(true);
        message.setCreatedAt(now);
        chatMessageRepository.save(message);

        return new CreateAmbassadorChatResponse(formatAmbassadorChatId(savedChat.getId()), savedChat.getStatus());
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<AmbassadorChatMessageResponse> ambassadorChatMessages(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity chat = findAmbassadorChat(chatId, currentUserId);
        return chatMessageRepository.findAllByTicketIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(this::toAmbassadorMessageResponse)
                .toList();
    }

    @PostMapping("/chats/{chatId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAmbassadorChatMessageResponse createAmbassadorChatMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId,
            @Valid @RequestBody CreateAmbassadorChatMessageRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity chat = findAmbassadorChat(chatId, currentUserId);
        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(chat.getId());
        message.setAuthorUserId(currentUserId);
        message.setMessageBody(request.contenido());
        message.setMessageType("TEXT");
        message.setVisibleToCustomer(true);
        message.setCreatedAt(OffsetDateTime.now());
        ClientChatMessageJpaEntity saved = chatMessageRepository.save(message);
        return new CreateAmbassadorChatMessageResponse(formatMessageId(saved.getId()), "enviado");
    }

    @PutMapping("/chats/{chatId}/read")
    public MessageResponse markAmbassadorChatRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity chat = findAmbassadorChat(chatId, currentUserId);
        ClientChatReadReceiptJpaEntity receipt =
                readReceiptRepository
                        .findByTicketIdAndUserId(chat.getId(), currentUserId)
                        .orElseGet(
                                () -> {
                                    ClientChatReadReceiptJpaEntity created = new ClientChatReadReceiptJpaEntity();
                                    created.setId(UUID.randomUUID());
                                    created.setTicketId(chat.getId());
                                    created.setUserId(currentUserId);
                                    return created;
                                });
        receipt.setReadAt(OffsetDateTime.now());
        readReceiptRepository.save(receipt);
        return new MessageResponse("Conversación marcada como leída");
    }

    @GetMapping("/reports/performance")
    public AmbassadorPerformanceReportResponse performanceReport(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        long clicks = totalReferralLinkClicks(ambassador.getId());
        long leads = leadRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).size();
        long conversions = referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassador.getId(), "activo");
        double conversionRate =
                leads == 0
                        ? 0
                        : BigDecimal.valueOf(conversions * 100.0 / leads)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
        return new AmbassadorPerformanceReportResponse(
                "mensual",
                clicks,
                leads,
                conversions,
                conversionRate,
                formatMoney(commissionRepository.sumAmountByAmbassadorId(ambassador.getId())));
    }

    @GetMapping("/reports/referrals")
    public List<AmbassadorReferralReportResponse> referralReport(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return referralRepository.findByAmbassadorId(ambassador.getId()).stream()
                .map(
                        referral ->
                                new AmbassadorReferralReportResponse(
                                        referralName(referral),
                                        valueOrDefault(referral.getReferralType(), "empresa"),
                                        valueOrDefault(referral.getStatus(), "prospecto"),
                                        commissionRepository.findByAmbassadorId(ambassador.getId()).stream()
                                                .filter(commission -> referral.getId().equals(commission.getAmbassadorReferralId()))
                                                .count(),
                                        formatMoney(sumReferralCommissions(ambassador.getId(), referral.getId()))))
                .toList();
    }

    @GetMapping("/reports/commissions")
    public CommissionSummaryResponse commissionReport(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return commissionSummary(userId);
    }

    @GetMapping("/reports/conversion-funnel")
    public ConversionFunnelResponse conversionFunnel(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return new ConversionFunnelResponse(
                totalReferralLinkClicks(ambassador.getId()),
                leadRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).size(),
                referralRepository.countByAmbassadorId(ambassador.getId()),
                referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassador.getId(), "activo"));
    }

    @GetMapping("/reports/export")
    public ReportExportResponse exportReport(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        return new ReportExportResponse(
                "https://techmarket.bo/reports/" + formatAmbassadorId(ambassador.getId()).toLowerCase(Locale.ROOT) + "-mensual.pdf");
    }

    @PostMapping("/ai/query")
    public AmbassadorAiQueryResponse aiQuery(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody AmbassadorAiQueryRequest request) {
        resolveAmbassador(findUser(parseUserId(userId)));
        String focus = request.consulta().toLowerCase(Locale.ROOT).contains("comision") ? "comisiones" : "conversion";
        return new AmbassadorAiQueryResponse(
                new AmbassadorAiAnswerResponse(
                        "Prioriza empresas con alta intención y onboarding incompleto.",
                        List.of(
                                "Contactar leads con probabilidad mayor a 70%",
                                "Reactivar prospectos sin respuesta en los últimos 5 días",
                                "Impulsar empresas que aún no publicaron productos"),
                        focus));
    }

    @GetMapping("/ai/insights")
    public AmbassadorAiInsightsResponse aiInsights(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        long leads = leadRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassador.getId()).size();
        long active = referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassador.getId(), "activo");
        int conversion = leads == 0 ? 0 : (int) Math.min(100, Math.round(active * 100.0 / leads));
        return new AmbassadorAiInsightsResponse(
                List.of(
                        new AmbassadorAiRadarResponse("Calidad de leads", Math.max(60, conversion + 13)),
                        new AmbassadorAiRadarResponse("Velocidad de seguimiento", 74),
                        new AmbassadorAiRadarResponse("Conversión", Math.max(50, conversion)),
                        new AmbassadorAiRadarResponse("Potencial de comisiones", 88)),
                "Hay buen potencial en empresas de hardware y servicios técnicos.");
    }

    @PostMapping("/ai/prospect-score")
    public ProspectScoreResponse prospectScore(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody ProspectScoreRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorLeadJpaEntity lead = findLead(request.leadId(), ambassador.getId());
        int score = lead.getCloseProbability();
        String probability = score >= 70 ? "alta" : score >= 45 ? "media" : "baja";
        return new ProspectScoreResponse(
                formatLeadId(lead.getId()),
                score,
                probability,
                List.of(
                        "Tiene catálogo tecnológico",
                        "Respondió en menos de 24 horas",
                        "Está en ciudad con alta demanda"));
    }

    @PostMapping("/ai/follow-up-suggestion")
    public FollowUpSuggestionResponse followUpSuggestion(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody FollowUpSuggestionRequest request) {
        AmbassadorJpaEntity ambassador = resolveAmbassador(findUser(parseUserId(userId)));
        AmbassadorReferralJpaEntity referral = findReferral(request.referidoId(), ambassador.getId());
        return new FollowUpSuggestionResponse(
                "Hola "
                        + valueOrDefault(referral.getContactName(), referralName(referral))
                        + ", vi que ya completaste tu perfil. El siguiente paso ideal es publicar tus primeros 3 productos para activar visibilidad en marketplace.",
                "whatsapp");
    }

    @PostMapping("/ai/improvement-plan")
    public ImprovementPlanResponse improvementPlan(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody ImprovementPlanRequest request) {
        resolveAmbassador(findUser(parseUserId(userId)));
        String area = valueOrDefault(request.area(), "conversion");
        return new ImprovementPlanResponse(
                new ImprovementPlanDetailResponse(
                        "Aumentar " + area + " de leads en 15%",
                        List.of(
                                "Contactar leads nuevos en menos de 2 horas",
                                "Usar mensajes personalizados por tipo de negocio",
                                "Agendar seguimiento a los 3 días si no completan registro"),
                        "30 días"));
    }

    private AmbassadorProfileResponse toProfileResponse(AmbassadorJpaEntity ambassador, UserJpaEntity user) {
        return new AmbassadorProfileResponse(
                formatAmbassadorId(ambassador.getId()),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                valueOrDefault(ambassador.getCountry(), "Bolivia"),
                ambassador.getCity(),
                ambassador.getAvatarUrl(),
                ambassador.getStatus(),
                valueOrDefault(ambassador.getLevel(), "Bronze"),
                ambassador.getReferralCode());
    }

    private ReferralLinkResponse toReferralLinkResponse(AmbassadorReferralLinkJpaEntity link) {
        return new ReferralLinkResponse(
                formatReferralLinkId(link.getId()),
                link.getName(),
                link.getCode(),
                link.getUrl(),
                link.getClicks(),
                link.getConversions(),
                link.isActive());
    }

    private ReferralLinkDetailResponse toReferralLinkDetailResponse(AmbassadorReferralLinkJpaEntity link) {
        double conversionRate =
                link.getClicks() == 0
                        ? 0
                        : BigDecimal.valueOf(link.getConversions() * 100.0 / link.getClicks())
                                .setScale(1, RoundingMode.HALF_UP)
                                .doubleValue();
        return new ReferralLinkDetailResponse(
                formatReferralLinkId(link.getId()),
                link.getName(),
                link.getCode(),
                link.getUrl(),
                link.getClicks(),
                link.getConversions(),
                conversionRate,
                link.isActive());
    }

    private AmbassadorReferralListResponse toReferralListResponse(AmbassadorReferralJpaEntity referral) {
        TenantJpaEntity tenant =
                referral.getTenantId() == null ? null : tenantRepository.findById(referral.getTenantId()).orElse(null);
        BigDecimal commission =
                commissionRepository.findByAmbassadorId(referral.getAmbassadorId()).stream()
                        .filter(item -> referral.getId().equals(item.getAmbassadorReferralId()))
                        .map(item -> parseAmount(item.getAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AmbassadorReferralListResponse(
                formatBusinessId(referral.getId()),
                tenant == null ? referral.getUsedCode() : tenant.getBusinessName(),
                "empresa",
                referral.getStatus(),
                referral.getCreatedAt() == null ? null : referral.getCreatedAt().toLocalDate().toString(),
                "Bs " + commission);
    }

    private AmbassadorReferralDetailResponse toReferralDetailResponse(AmbassadorReferralJpaEntity referral) {
        return new AmbassadorReferralDetailResponse(
                formatBusinessId(referral.getId()),
                referralName(referral),
                valueOrDefault(referral.getReferralType(), "empresa"),
                referral.getStatus(),
                new ReferralContactResponse(referral.getContactName(), referral.getPhone(), referral.getEmail()),
                referral.getCreatedAt() == null ? null : referral.getCreatedAt().toLocalDate().toString(),
                referral.getLastActivityAt());
    }

    private AmbassadorReferralJpaEntity findReferral(String referralId, UUID ambassadorId) {
        return referralRepository
                .findByIdAndAmbassadorId(parsePrefixedUuid(referralId, "BUS-"), ambassadorId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral not found"));
    }

    private AmbassadorReferralJpaEntity findReferralByOnboarding(String onboardingId, UUID ambassadorId) {
        return referralRepository
                .findByIdAndAmbassadorId(parsePrefixedUuid(onboardingId, "ONB-"), ambassadorId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Onboarding not found"));
    }

    private AmbassadorLeadJpaEntity findLead(String leadId, UUID ambassadorId) {
        return leadRepository
                .findByIdAndAmbassadorId(parsePrefixedUuid(leadId, "LEAD-"), ambassadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
    }

    private AmbassadorCommissionJpaEntity findCommission(String commissionId, UUID ambassadorId) {
        return commissionRepository
                .findByIdAndAmbassadorId(parsePrefixedUuid(commissionId, "COM-"), ambassadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
    }

    private AmbassadorNetworkResponse toNetworkResponse(AmbassadorJpaEntity ambassador) {
        return new AmbassadorNetworkResponse(
                formatAmbassadorId(ambassador.getId()),
                ambassadorUser(ambassador).map(this::fullName).orElse("Embajador"),
                valueOrDefault(ambassador.getLevel(), "Bronze"),
                referralRepository.countByAmbassadorId(ambassador.getId()),
                valueOrDefault(ambassador.getStatus(), "Activo"));
    }

    private AmbassadorNetworkNodeResponse toNetworkNodeResponse(AmbassadorJpaEntity ambassador) {
        return new AmbassadorNetworkNodeResponse(
                formatAmbassadorId(ambassador.getId()),
                ambassadorUser(ambassador).map(this::fullName).orElse("Embajador"),
                valueOrDefault(ambassador.getLevel(), "Bronze"));
    }

    private AmbassadorChatSummaryResponse toAmbassadorChatSummary(ClientChatJpaEntity chat, UUID currentUserId) {
        return new AmbassadorChatSummaryResponse(
                formatAmbassadorChatId(chat.getId()),
                valueOrDefault(chat.getSubject(), "Conversación"),
                chatMessageRepository
                        .findTopByTicketIdOrderByCreatedAtDesc(chat.getId())
                        .map(ClientChatMessageJpaEntity::getMessageBody)
                        .orElse(""),
                unreadMessages(chat.getId(), currentUserId));
    }

    private AmbassadorChatMessageResponse toAmbassadorMessageResponse(ClientChatMessageJpaEntity message) {
        return new AmbassadorChatMessageResponse(
                formatMessageId(message.getId()),
                "embajador",
                message.getMessageBody(),
                message.getCreatedAt());
    }

    private ClientChatJpaEntity findAmbassadorChat(String chatId, UUID userId) {
        return chatRepository
                .findByIdAndCustomerUserIdAndTicketType(
                        parsePrefixedUuid(chatId, "CHT-AMB-"), userId, AMBASSADOR_CHAT_TYPE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
    }

    private int unreadMessages(UUID chatId, UUID currentUserId) {
        return readReceiptRepository
                .findByTicketIdAndUserId(chatId, currentUserId)
                .map(ClientChatReadReceiptJpaEntity::getReadAt)
                .map(readAt -> chatMessageRepository.countByTicketIdAndAuthorUserIdNotAndCreatedAtAfter(
                        chatId, currentUserId, readAt))
                .orElseGet(() -> chatMessageRepository.countByTicketIdAndAuthorUserIdNot(chatId, currentUserId))
                .intValue();
    }

    private CommissionResponse toCommissionResponse(AmbassadorCommissionJpaEntity commission) {
        return new CommissionResponse(
                formatCommissionId(commission.getId()),
                referralNameById(commission.getAmbassadorReferralId()),
                conceptFor(commission),
                formatMoney(parseAmount(commission.getAmount())),
                valueOrDefault(commission.getStatus(), "pendiente"),
                commission.getGeneratedAt() == null ? null : commission.getGeneratedAt().toLocalDate().toString());
    }

    private String referralNameById(UUID referralId) {
        if (referralId == null) {
            return "Referido";
        }
        return referralRepository.findById(referralId).map(this::referralName).orElse("Referido");
    }

    private String conceptFor(AmbassadorCommissionJpaEntity commission) {
        return valueOrDefault(
                commission.getEventType(), valueOrDefault(commission.getReferenceType(), "Comisión generada"));
    }

    private BigDecimal sumCommissions(List<AmbassadorCommissionJpaEntity> commissions, String status) {
        return commissions.stream()
                .filter(commission -> status == null || status.equalsIgnoreCase(valueOrDefault(commission.getStatus(), "")))
                .map(commission -> parseAmount(commission.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumWithdrawals(UUID ambassadorId, String status) {
        return withdrawalRepository.findAllByAmbassadorId(ambassadorId).stream()
                .filter(withdrawal -> status == null || status.equalsIgnoreCase(valueOrDefault(withdrawal.getStatus(), "")))
                .map(withdrawal -> parseAmount(withdrawal.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long totalReferralLinkClicks(UUID ambassadorId) {
        return referralLinkRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassadorId).stream()
                .mapToLong(AmbassadorReferralLinkJpaEntity::getClicks)
                .sum();
    }

    private BigDecimal sumReferralCommissions(UUID ambassadorId, UUID referralId) {
        return commissionRepository.findByAmbassadorId(ambassadorId).stream()
                .filter(commission -> referralId.equals(commission.getAmbassadorReferralId()))
                .map(commission -> parseAmount(commission.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private java.util.Optional<UserJpaEntity> ambassadorUser(AmbassadorJpaEntity ambassador) {
        return ambassador.getUserId() == null ? java.util.Optional.empty() : userRepository.findById(ambassador.getUserId());
    }

    private String fullName(UserJpaEntity user) {
        String name = (valueOrDefault(user.getFirstName(), "") + " " + valueOrDefault(user.getLastName(), "")).trim();
        return name.isBlank() ? valueOrDefault(user.getEmail(), "Usuario") : name;
    }

    private String last4(String value) {
        String normalized = value == null ? "" : value.replaceAll("\\D", "");
        return normalized.length() <= 4 ? normalized : normalized.substring(normalized.length() - 4);
    }

    private void createActivity(UUID referralId, String type, String description, OffsetDateTime createdAt) {
        AmbassadorReferralActivityJpaEntity activity = new AmbassadorReferralActivityJpaEntity();
        activity.setId(UUID.randomUUID());
        activity.setAmbassadorReferralId(referralId);
        activity.setActivityType(type);
        activity.setDescription(description);
        activity.setCreatedAt(createdAt);
        activityRepository.save(activity);
    }

    private String referralName(AmbassadorReferralJpaEntity referral) {
        if (referral.getName() != null && !referral.getName().isBlank()) {
            return referral.getName();
        }
        TenantJpaEntity tenant =
                referral.getTenantId() == null ? null : tenantRepository.findById(referral.getTenantId()).orElse(null);
        return tenant == null ? valueOrDefault(referral.getUsedCode(), "Referido") : tenant.getBusinessName();
    }

    private int onboardingProgress(UUID referralId) {
        long completed =
                defaultMilestones().stream()
                        .filter(
                                milestone ->
                                        milestoneRepository
                                                .findByAmbassadorReferralIdAndMilestoneCode(
                                                        referralId, milestone.id())
                                                .map(AmbassadorOnboardingMilestoneJpaEntity::isCompleted)
                                                .orElse(false))
                        .count();
        return (int) Math.round(completed * 100.0 / defaultMilestones().size());
    }

    private List<OnboardingStepResponse> defaultOnboardingSteps(UUID referralId) {
        return defaultMilestones().stream()
                .map(
                        milestone ->
                                new OnboardingStepResponse(
                                        milestone.id(),
                                        milestone.nombre(),
                                        milestoneRepository
                                                .findByAmbassadorReferralIdAndMilestoneCode(
                                                        referralId, milestone.id())
                                                .map(AmbassadorOnboardingMilestoneJpaEntity::isCompleted)
                                                .orElse(false)))
                .toList();
    }

    private List<MilestoneResponse> defaultMilestones() {
        return List.of(
                new MilestoneResponse("MLS-001", "Registro completado", 1),
                new MilestoneResponse("MLS-002", "Primera publicación", 2),
                new MilestoneResponse("MLS-003", "Primera venta", 3));
    }

    private AmbassadorReferralLinkJpaEntity findReferralLink(String linkId, UUID ambassadorId) {
        return referralLinkRepository
                .findByIdAndAmbassadorId(parsePrefixedUuid(linkId, "REFLINK-"), ambassadorId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Referral link not found"));
    }

    private AmbassadorJpaEntity resolveAmbassador(UserJpaEntity user) {
        return ambassadorRepository
                .findByUserId(user.getId())
                .orElseGet(
                        () -> {
                            AmbassadorJpaEntity created = new AmbassadorJpaEntity();
                            created.setId(UUID.randomUUID());
                            created.setUserId(user.getId());
                            created.setReferralCode(defaultReferralCode(user));
                            created.setStatus("Activo");
                            created.setLevel("Bronze");
                            created.setCountry("Bolivia");
                            created.setLanguage("es");
                            created.setActivatedAt(OffsetDateTime.now());
                            return ambassadorRepository.save(created);
                        });
    }

    private UserJpaEntity findUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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

    private String buildReferralCode(AmbassadorJpaEntity ambassador, String city, String segment) {
        String base = valueOrDefault(ambassador.getReferralCode(), "AMB");
        String suffix = slug(valueOrDefault(city, segment));
        if (suffix.isBlank()) {
            suffix = ambassador.getId().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        }
        return (base + "-" + suffix).toUpperCase(Locale.ROOT);
    }

    private String defaultReferralCode(UserJpaEntity user) {
        String base = slug(valueOrDefault(user.getFirstName(), user.getEmail()));
        if (base.isBlank()) {
            base = "AMB";
        }
        return (base + "-" + user.getId().toString().substring(0, 6)).toUpperCase(Locale.ROOT);
    }

    private String slug(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.toUpperCase(Locale.ROOT);
    }

    private BigDecimal parseAmount(String amount) {
        if (amount == null || amount.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(amount.replace("Bs", "").replace(",", "").trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private String formatMoney(BigDecimal amount) {
        return "Bs " + amount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String referralUrl(String code) {
        return "https://techmarket.bo/register?ref=" + code;
    }

    private String formatAmbassadorId(UUID id) {
        return "AMB-" + id;
    }

    private String formatReferralLinkId(UUID id) {
        return "REFLINK-" + id;
    }

    private String formatBusinessId(UUID id) {
        return "BUS-" + id;
    }

    private String formatOnboardingId(UUID id) {
        return "ONB-" + id;
    }

    private String formatLeadId(UUID id) {
        return "LEAD-" + id;
    }

    private String formatCommissionId(UUID id) {
        return "COM-" + id;
    }

    private String formatWithdrawalId(UUID id) {
        return "WDR-" + id;
    }

    private String formatPayoutMethodId(UUID id) {
        return "PAYM-" + id;
    }

    private String formatAmbassadorInvitationId(UUID id) {
        return "INV-AMB-" + id;
    }

    private String formatAmbassadorChatId(UUID id) {
        return "CHT-AMB-" + id;
    }

    private String formatMessageId(UUID id) {
        return "MSG-" + id;
    }

    public record AmbassadorProfileResponse(
            String id,
            String email,
            String nombre,
            String apellido,
            String telefono,
            String pais,
            String ciudad,
            String avatar,
            String estado,
            String nivel,
            String codigoReferido) {}

    public record UpdateAmbassadorProfileRequest(
            @NotBlank String nombre,
            @NotBlank String apellido,
            String telefono,
            String ciudad,
            String descripcion) {}

    public record UpdatePhotoRequest(@NotBlank String url) {}

    public record PhotoResponse(String url) {}

    public record AmbassadorStatsResponse(
            long negociosReferidos,
            long negociosActivos,
            double conversionRate,
            String comisionesTotales,
            String nivel) {}

    public record AmbassadorSettingsResponse(
            boolean notificacionesEmail,
            boolean notificacionesPush,
            boolean mostrarPerfilPublico,
            String idioma) {}

    public record UpdateAmbassadorSettingsRequest(
            boolean notificacionesEmail, boolean notificacionesPush, boolean mostrarPerfilPublico) {}

    public record ReferralLinkResponse(
            String id, String nombre, String codigo, String url, int clics, int conversiones, boolean activo) {}

    public record CreateReferralLinkRequest(@NotBlank String nombre, String segmento, String ciudad) {}

    public record CreateReferralLinkResponse(String id, String codigo, String url) {}

    public record ReferralLinkDetailResponse(
            String id,
            String nombre,
            String codigo,
            String url,
            int clics,
            int conversiones,
            double conversionRate,
            boolean activo) {}

    public record UpdateReferralLinkRequest(@NotBlank String nombre, String segmento) {}

    public record UpdateReferralLinkStatusRequest(boolean activo) {}

    public record ReferralLinkStatusResponse(String id, boolean activo) {}

    public record ReferralLinkQrResponse(String qrUrl) {}

    public record ReferralCodeResponse(String codigo, String tipo, long usos, boolean activo) {}

    public record AmbassadorReferralListResponse(
            String id,
            String nombre,
            String tipo,
            String estado,
            String fechaRegistro,
            String comisionGenerada) {}

    public record CreateReferralRequest(
            @NotBlank String nombre,
            @NotBlank String tipo,
            String contacto,
            String telefono,
            String email,
            String ciudad) {}

    public record CreateReferralResponse(String id, String estado, String mensaje) {}

    public record AmbassadorReferralDetailResponse(
            String id,
            String nombre,
            String tipo,
            String estado,
            ReferralContactResponse contacto,
            String fechaRegistro,
            OffsetDateTime ultimaActividad) {}

    public record ReferralContactResponse(String nombre, String telefono, String email) {}

    public record UpdateReferralRequest(String contacto, String telefono, String ciudad) {}

    public record UpdateReferralStatusRequest(@NotBlank String estado) {}

    public record ReferralStatusResponse(String id, String estado) {}

    public record ReferralActivityResponse(
            String id, String tipo, String descripcion, OffsetDateTime fecha) {}

    public record CreateReferralNoteRequest(@NotBlank String nota) {}

    public record CreateReferralNoteResponse(String id, String mensaje) {}

    public record ReferralNoteResponse(String id, String nota, OffsetDateTime fecha) {}

    public record CreateReferralFileRequest(@NotBlank String url) {}

    public record ReferralFileResponse(String id, String url) {}

    public record OnboardingSummaryResponse(
            String id, String referidoId, String nombre, int progreso, String estado) {}

    public record OnboardingDetailResponse(
            String id, String referido, String estado, int progreso, List<OnboardingStepResponse> pasos) {}

    public record OnboardingStepResponse(String id, String nombre, boolean completado) {}

    public record CreateOnboardingTaskRequest(@NotBlank String titulo, LocalDate fechaLimite) {}

    public record CreateOnboardingTaskResponse(String id, String mensaje) {}

    public record OnboardingTaskResponse(
            String id, String titulo, String estado, LocalDate fechaLimite) {}

    public record UpdateTaskStatusRequest(@NotBlank String estado) {}

    public record OnboardingTaskStatusResponse(String id, String estado) {}

    public record CreateReminderRequest(OffsetDateTime fecha, @NotBlank String mensaje) {}

    public record ReminderResponse(String id, String mensaje) {}

    public record MilestoneResponse(String id, String nombre, int orden) {}

    public record LeadSummaryResponse(
            String id, String nombre, String tipo, String estado, String fuente) {}

    public record CreateLeadRequest(
            @NotBlank String nombre, String tipo, String contacto, String telefono, String fuente) {}

    public record CreateLeadResponse(String id, String mensaje) {}

    public record LeadDetailResponse(
            String id, String nombre, String tipo, String estado, int probabilidadCierre) {}

    public record UpdateLeadRequest(
            @NotBlank String nombre, String tipo, String contacto, String telefono, String fuente) {}

    public record UpdateLeadStatusRequest(@NotBlank String estado) {}

    public record LeadStatusResponse(String id, String estado) {}

    public record ConvertLeadResponse(String referidoId, String mensaje) {}

    public record CommissionResponse(
            String id, String referido, String concepto, String monto, String estado, String fecha) {}

    public record CommissionSummaryResponse(String totalGenerado, String disponible, String pendiente, String pagado) {}

    public record CommissionDetailResponse(
            String id,
            String referido,
            String concepto,
            String monto,
            int porcentaje,
            String estado,
            OffsetDateTime fechaGeneracion) {}

    public record CommissionDisputeRequest(@NotBlank String motivo, String descripcion) {}

    public record CommissionDisputeResponse(String id, String estado) {}

    public record AmbassadorWalletResponse(String saldoDisponible, String saldoPendiente, String totalRetirado) {}

    public record WithdrawalRequest(@NotBlank String monto, @NotBlank String metodoPagoId) {}

    public record WithdrawalResponse(String id, String estado, LocalDate fechaEstimada) {}

    public record PayoutResponse(String id, String monto, String estado, String fecha) {}

    public record PayoutMethodResponse(
            String id, String tipo, String banco, String ultimos4, boolean predeterminado) {}

    public record CreatePayoutMethodRequest(
            @NotBlank String tipo, String banco, @NotBlank String numeroCuenta, String titular) {}

    public record CreatePayoutMethodResponse(String id, String mensaje) {}

    public record AmbassadorNetworkResponse(
            String id, String nombre, String nivel, long referidos, String estado) {}

    public record AmbassadorNetworkTreeResponse(
            String id, String nombre, String nivel, List<AmbassadorNetworkNodeResponse> subEmbajadores) {}

    public record AmbassadorNetworkNodeResponse(String id, String nombre, String nivel) {}

    public record CreateAmbassadorInvitationRequest(
            @NotBlank String email, @NotBlank String nombre, String telefono) {}

    public record CreateAmbassadorInvitationResponse(String id, String estado) {}

    public record AmbassadorInvitationResponse(String id, String email, String estado) {}

    public record AmbassadorRankingResponse(
            int posicion, String id, String nombre, long conversiones, String comisiones) {}

    public record AmbassadorChatSummaryResponse(
            String id, String participante, String ultimoMensaje, int mensajesSinLeer) {}

    public record CreateAmbassadorChatRequest(
            @NotBlank String participanteId, @NotBlank String mensajeInicial) {}

    public record CreateAmbassadorChatResponse(String id, String estado) {}

    public record AmbassadorChatMessageResponse(
            String id, String remitente, String contenido, OffsetDateTime fecha) {}

    public record CreateAmbassadorChatMessageRequest(@NotBlank String contenido) {}

    public record CreateAmbassadorChatMessageResponse(String id, String estado) {}

    public record AmbassadorPerformanceReportResponse(
            String periodo, long clics, long leads, long conversiones, double conversionRate, String comisiones) {}

    public record AmbassadorReferralReportResponse(
            String referido, String tipo, String estado, long ventasGeneradas, String comision) {}

    public record ConversionFunnelResponse(long clics, long leads, long registros, long activos) {}

    public record ReportExportResponse(String downloadUrl) {}

    public record AmbassadorAiQueryRequest(@NotBlank String consulta) {}

    public record AmbassadorAiQueryResponse(AmbassadorAiAnswerResponse respuesta) {}

    public record AmbassadorAiAnswerResponse(String resumen, List<String> acciones, String foco) {}

    public record AmbassadorAiInsightsResponse(List<AmbassadorAiRadarResponse> radar, String recomendacion) {}

    public record AmbassadorAiRadarResponse(String etiqueta, int valor) {}

    public record ProspectScoreRequest(@NotBlank String leadId) {}

    public record ProspectScoreResponse(
            String leadId, int score, String probabilidadCierre, List<String> motivos) {}

    public record FollowUpSuggestionRequest(@NotBlank String referidoId) {}

    public record FollowUpSuggestionResponse(String mensajeSugerido, String canalRecomendado) {}

    public record ImprovementPlanRequest(@NotBlank String area) {}

    public record ImprovementPlanResponse(ImprovementPlanDetailResponse plan) {}

    public record ImprovementPlanDetailResponse(
            String objetivo, List<String> acciones, String tiempoEstimado) {}

    public record MessageResponse(String mensaje) {}
}
