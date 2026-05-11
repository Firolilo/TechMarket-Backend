package com.techmarket.techmarket.shared.config;

import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorMissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOpportunityJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorMissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOpportunitySpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralSpringDataRepository;
import com.techmarket.techmarket.users.domain.model.User;
import com.techmarket.techmarket.users.domain.port.UserRepositoryPort;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    private final UserRepositoryPort userRepository;
    private final AmbassadorRepositoryPort ambassadorRepository;
    private final AmbassadorReferralSpringDataRepository referralRepo;
    private final AmbassadorCommissionSpringDataRepository commissionRepo;
    private final AmbassadorOpportunitySpringDataRepository opportunityRepo;
    private final AmbassadorMissionSpringDataRepository missionRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepositoryPort userRepository,
            AmbassadorRepositoryPort ambassadorRepository,
            AmbassadorReferralSpringDataRepository referralRepo,
            AmbassadorCommissionSpringDataRepository commissionRepo,
            AmbassadorOpportunitySpringDataRepository opportunityRepo,
            AmbassadorMissionSpringDataRepository missionRepo,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.referralRepo = referralRepo;
        this.commissionRepo = commissionRepo;
        this.opportunityRepo = opportunityRepo;
        this.missionRepo = missionRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            log.info("Seed: usuario admin ya existe, omitiendo.");
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        UUID userId = UUID.randomUUID();
        UUID ambassadorId = UUID.randomUUID();

        User user =
                new User(
                        userId,
                        "Admin",
                        "TechMarket",
                        ADMIN_EMAIL,
                        passwordEncoder.encode(ADMIN_PASSWORD),
                        "ACTIVE",
                        now,
                        now);
        userRepository.save(user);

        Ambassador ambassador =
                new Ambassador(ambassadorId, userId, "AF-ADMIN", "ACTIVE", "BRONCE", now);
        ambassadorRepository.save(ambassador);

        seedReferralsAndCommissions(ambassadorId, now);
        seedOpportunities(ambassadorId, now);
        seedMissions(ambassadorId, now);

        log.info("Seed completo: admin@gmail.com, ambassador={}, referrals+commissions+opportunities+missions creados.", ambassadorId);
    }

    private void seedReferralsAndCommissions(UUID ambassadorId, OffsetDateTime now) {
        // ── Nivel 1: referidos directos del embajador ──────────────────────
        UUID r1 = UUID.randomUUID();
        referralRepo.save(referral(r1, ambassadorId, "TechStore Bolivia", "La Paz", "HARDWARE", "ACTIVE", now.minusMonths(5)));

        UUID r2 = UUID.randomUUID();
        referralRepo.save(referral(r2, ambassadorId, "SoftWave Solutions", "Cochabamba", "SOFTWARE", "ACTIVE", now.minusMonths(4)));

        UUID r3 = UUID.randomUUID();
        referralRepo.save(referral(r3, ambassadorId, "DataLink Servicios", "Santa Cruz", "SERVICES", "ACTIVE", now.minusMonths(3)));

        UUID r4 = UUID.randomUUID();
        referralRepo.save(referral(r4, ambassadorId, "Innovatech SRL", "La Paz", "SOFTWARE", "ACTIVE", now.minusMonths(2)));

        // ── Nivel 2: referidos por TechStore y SoftWave ───────────────────
        UUID r5 = UUID.randomUUID();
        referralRepo.save(referral(r5, ambassadorId, "BizConnect Bolivia", "Santa Cruz", "SERVICES", "ACTIVE", now.minusMonths(3)));

        UUID r6 = UUID.randomUUID();
        referralRepo.save(referral(r6, ambassadorId, "CloudPro Sistemas", "Cochabamba", "SOFTWARE", "ACTIVE", now.minusMonths(2)));

        UUID r7 = UUID.randomUUID();
        referralRepo.save(referral(r7, ambassadorId, "MegaRed Comunicaciones", "La Paz", "HARDWARE", "ACTIVE", now.minusMonths(2)));

        // ── Nivel 3: referidos por nivel 2 ────────────────────────────────
        UUID r8 = UUID.randomUUID();
        referralRepo.save(referral(r8, ambassadorId, "AlphaData Corp", "Santa Cruz", "HARDWARE", "ACTIVE", now.minusMonths(1)));

        UUID r9 = UUID.randomUUID();
        referralRepo.save(referral(r9, ambassadorId, "NetSolutions Andina", "Oruro", "SERVICES", "ACTIVE", now.minusMonths(1)));

        // ── Comisiones nivel 1 — spread en 5 meses ────────────────────────
        List<CommissionSeed> seeds = List.of(
                // R1 – TechStore Bolivia (HARDWARE)
                new CommissionSeed(r1, "850.00",  "SUBSCRIPTION", "HARDWARE", "LEVEL1", now.minusMonths(5).plusDays(5)),
                new CommissionSeed(r1, "920.00",  "RENEWAL",      "HARDWARE", "LEVEL1", now.minusMonths(4).plusDays(3)),
                new CommissionSeed(r1, "970.00",  "RENEWAL",      "HARDWARE", "LEVEL1", now.minusMonths(3).plusDays(7)),
                new CommissionSeed(r1, "1050.00", "RENEWAL",      "HARDWARE", "LEVEL1", now.minusMonths(2).plusDays(4)),
                new CommissionSeed(r1, "1120.00", "RENEWAL",      "HARDWARE", "LEVEL1", now.minusWeeks(3).plusDays(1)),
                new CommissionSeed(r1, "1180.00", "RENEWAL",      "HARDWARE", "LEVEL1", now.minusWeeks(1).plusDays(2)),

                // R2 – SoftWave Solutions (SOFTWARE)
                new CommissionSeed(r2, "650.00",  "SUBSCRIPTION", "SOFTWARE", "LEVEL1", now.minusMonths(4).plusDays(7)),
                new CommissionSeed(r2, "710.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusMonths(3).plusDays(4)),
                new CommissionSeed(r2, "760.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusMonths(2).plusDays(2)),
                new CommissionSeed(r2, "800.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusWeeks(4).plusDays(1)),
                new CommissionSeed(r2, "845.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusWeeks(2).plusDays(3)),
                new CommissionSeed(r2, "890.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusDays(4)),

                // R3 – DataLink Servicios (SERVICES)
                new CommissionSeed(r3, "420.00",  "SUBSCRIPTION", "SERVICES", "LEVEL1", now.minusMonths(3).plusDays(10)),
                new CommissionSeed(r3, "480.00",  "RENEWAL",      "SERVICES", "LEVEL1", now.minusMonths(2).plusDays(6)),
                new CommissionSeed(r3, "510.00",  "RENEWAL",      "SERVICES", "LEVEL1", now.minusWeeks(3).plusDays(2)),
                new CommissionSeed(r3, "545.00",  "RENEWAL",      "SERVICES", "LEVEL1", now.minusWeeks(1).plusDays(1)),

                // R4 – Innovatech SRL (SOFTWARE)
                new CommissionSeed(r4, "730.00",  "SUBSCRIPTION", "SOFTWARE", "LEVEL1", now.minusMonths(2).plusDays(3)),
                new CommissionSeed(r4, "780.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusWeeks(2).plusDays(1)),
                new CommissionSeed(r4, "820.00",  "RENEWAL",      "SOFTWARE", "LEVEL1", now.minusDays(5)),

                // ── Comisiones nivel 2 ─────────────────────────────────────
                // R5 – BizConnect Bolivia
                new CommissionSeed(r5, "480.00",  "SUBSCRIPTION", "SERVICES", "LEVEL2", now.minusMonths(3).plusDays(8)),
                new CommissionSeed(r5, "520.00",  "RENEWAL",      "SERVICES", "LEVEL2", now.minusMonths(2).plusDays(5)),
                new CommissionSeed(r5, "560.00",  "RENEWAL",      "SERVICES", "LEVEL2", now.minusWeeks(3).plusDays(3)),
                new CommissionSeed(r5, "600.00",  "RENEWAL",      "SERVICES", "LEVEL2", now.minusDays(6)),

                // R6 – CloudPro Sistemas
                new CommissionSeed(r6, "610.00",  "SUBSCRIPTION", "SOFTWARE", "LEVEL2", now.minusMonths(2).plusDays(9)),
                new CommissionSeed(r6, "655.00",  "RENEWAL",      "SOFTWARE", "LEVEL2", now.minusWeeks(4).plusDays(2)),
                new CommissionSeed(r6, "695.00",  "RENEWAL",      "SOFTWARE", "LEVEL2", now.minusWeeks(2)),
                new CommissionSeed(r6, "730.00",  "RENEWAL",      "SOFTWARE", "LEVEL2", now.minusDays(3)),

                // R7 – MegaRed Comunicaciones
                new CommissionSeed(r7, "390.00",  "SUBSCRIPTION", "HARDWARE", "LEVEL2", now.minusMonths(2).plusDays(12)),
                new CommissionSeed(r7, "430.00",  "RENEWAL",      "HARDWARE", "LEVEL2", now.minusWeeks(3)),
                new CommissionSeed(r7, "465.00",  "RENEWAL",      "HARDWARE", "LEVEL2", now.minusWeeks(1)),

                // ── Comisiones nivel 3 ─────────────────────────────────────
                // R8 – AlphaData Corp
                new CommissionSeed(r8, "310.00",  "SUBSCRIPTION", "HARDWARE", "LEVEL3", now.minusMonths(1).plusDays(4)),
                new CommissionSeed(r8, "340.00",  "RENEWAL",      "HARDWARE", "LEVEL3", now.minusWeeks(2).plusDays(2)),
                new CommissionSeed(r8, "370.00",  "RENEWAL",      "HARDWARE", "LEVEL3", now.minusDays(7)),

                // R9 – NetSolutions Andina
                new CommissionSeed(r9, "280.00",  "SUBSCRIPTION", "SERVICES", "LEVEL3", now.minusMonths(1).plusDays(6)),
                new CommissionSeed(r9, "310.00",  "RENEWAL",      "SERVICES", "LEVEL3", now.minusWeeks(2)),
                new CommissionSeed(r9, "335.00",  "RENEWAL",      "SERVICES", "LEVEL3", now.minusDays(2))
        );

        for (CommissionSeed s : seeds) {
            commissionRepo.save(commission(ambassadorId, s.referralId(), s.amount(), s.eventType(),
                    s.referenceType(), s.attributionType(), s.date()));
        }
    }

    private AmbassadorReferralJpaEntity referral(
            UUID id, UUID ambassadorId, String name, String city,
            String type, String status, OffsetDateTime createdAt) {
        AmbassadorReferralJpaEntity e = new AmbassadorReferralJpaEntity();
        e.setId(id);
        e.setAmbassadorId(ambassadorId);
        e.setName(name);
        e.setCity(city);
        e.setReferralType(type);
        e.setStatus(status);
        e.setCreatedAt(createdAt);
        e.setLastActivityAt(createdAt.plusDays(2));
        return e;
    }

    private AmbassadorCommissionJpaEntity commission(
            UUID ambassadorId, UUID referralId, String amount,
            String eventType, String referenceType, String attributionType,
            OffsetDateTime generatedAt) {
        AmbassadorCommissionJpaEntity e = new AmbassadorCommissionJpaEntity();
        e.setId(UUID.randomUUID());
        e.setAmbassadorId(ambassadorId);
        e.setAmbassadorReferralId(referralId);
        e.setAmount(amount);
        e.setEventType(eventType);
        e.setReferenceType(referenceType);
        e.setAttributionType(attributionType);
        e.setStatus("CONFIRMED");
        e.setGeneratedAt(generatedAt);
        return e;
    }

    private record CommissionSeed(
            UUID referralId, String amount, String eventType,
            String referenceType, String attributionType, OffsetDateTime date) {}

    private void seedOpportunities(UUID ambassadorId, OffsetDateTime now) {
        List<AmbassadorOpportunityJpaEntity> opps = List.of(
            opportunity(ambassadorId,
                "HARDWARE", "Equipetrol, Santa Cruz",
                "Alta demanda de equipos de oficina en empresas nuevas del área. Zona con crecimiento empresarial acelerado y baja penetración de proveedores TI.",
                "alto", "Demanda no cubierta", "nueva", false,
                now.minusDays(2)),
            opportunity(ambassadorId,
                "SOFTWARE", "Plan 3000, Santa Cruz",
                "3 empresas de logística buscan software de gestión sin proveedor actual. Necesidad crítica de ERP básico detectada en búsquedas recientes.",
                "alto", "Búsqueda activa detectada", "nueva", true,
                now.minusDays(5)),
            opportunity(ambassadorId,
                "SERVICES", "Sopocachi, La Paz",
                "Demanda creciente de soporte técnico en zona empresarial. Varios negocios con equipos sin contrato de mantenimiento activo.",
                "medio", "Tendencia de mercado", "enSeguimiento", false,
                now.minusDays(8)),
            opportunity(ambassadorId,
                "HARDWARE", "Miraflores, La Paz",
                "Nuevas oficinas en construcción requieren equipamiento tecnológico completo. Oportunidad de entrada temprana antes de apertura.",
                "medio", "Nuevas aperturas", "nueva", false,
                now.minusDays(12)),
            opportunity(ambassadorId,
                "SOFTWARE", "Cochabamba Centro",
                "Empresas comerciales sin sistema de facturación electrónica. Requisito regulatorio inminente genera urgencia de adopción.",
                "alto", "Obligación regulatoria", "nueva", true,
                now.minusDays(15)),
            opportunity(ambassadorId,
                "SERVICES", "Calacoto, La Paz",
                "Zona residencial premium con demanda de mantenimiento tecnológico para home offices. Mercado con capacidad de pago alta.",
                "bajo", "Demanda estacional", "atendida", false,
                now.minusDays(20))
        );
        opportunityRepo.saveAll(opps);
    }

    private AmbassadorOpportunityJpaEntity opportunity(
            UUID ambassadorId, String type, String zone, String description,
            String potential, String dataSource, String status, boolean isSaved,
            OffsetDateTime detectedAt) {
        AmbassadorOpportunityJpaEntity e = new AmbassadorOpportunityJpaEntity();
        e.setId(UUID.randomUUID());
        e.setAmbassadorId(ambassadorId);
        e.setOpportunityType(type);
        e.setZone(zone);
        e.setDescription(description);
        e.setPotential(potential);
        e.setDataSource(dataSource);
        e.setStatus(status);
        e.setSaved(isSaved);
        e.setDetectedAt(detectedAt);
        e.setUpdatedAt(detectedAt);
        return e;
    }

    private void seedMissions(UUID ambassadorId, OffsetDateTime now) {
        List<AmbassadorMissionJpaEntity> missions = List.of(
            mission(ambassadorId,
                "Registra tu primer referido",
                "Invita a una empresa a registrarse en TechMarket usando tu código de embajador.",
                "Comisión de Bs 50 al confirmar el registro",
                "hardware", "alta", "completada",
                "Busca prospectos en tu red|Envía tu código de referido|Confirma el registro en plataforma",
                "Referido confirmado y activo en plataforma",
                BigDecimal.ONE, now.minusMonths(3)),
            mission(ambassadorId,
                "Completa tu perfil de embajador",
                "Agrega foto, descripción profesional y configura tu método de pago preferido.",
                "Acceso a comisiones avanzadas y tier Plata",
                "software", "normal", "completada",
                "Agrega foto de perfil|Escribe tu descripción|Configura método de pago",
                "Perfil completado al 100%",
                BigDecimal.ONE, now.minusMonths(2)),
            mission(ambassadorId,
                "Refiere 3 empresas de hardware",
                "Trae al menos 3 negocios del sector tecnológico en hardware para ampliar tu red de nivel 1.",
                "Bono de Bs 200 al completar los 3 referidos",
                "hardware", "alta", "enProgreso",
                "Identifica 3 negocios de hardware en tu zona|Preséntales TechMarket|Confirma sus registros activos",
                "3 referidos de hardware con estado ACTIVE",
                new BigDecimal("0.66"), now.minusMonths(1)),
            mission(ambassadorId,
                "Activa un referido en software",
                "Ayuda a una empresa de software a completar su proceso de onboarding en la plataforma.",
                "Comisión extra del 2% en sus primeras 3 renovaciones",
                "software", "normal", "enProgreso",
                "Selecciona un prospecto de software|Guía el proceso de onboarding|Confirma la activación completa",
                "Referido con onboarding al 100% y primer pago procesado",
                new BigDecimal("0.50"), now.minusWeeks(3)),
            mission(ambassadorId,
                "Capta 2 clientes de servicios técnicos",
                "Refiere empresas que necesiten soporte o mantenimiento tecnológico continuo.",
                "Bs 30 por cliente activo + 5% en contratos anuales",
                "servicios", "normal", "disponible",
                "Identifica empresas con necesidad de soporte|Presenta el catálogo de servicios TechMarket|Acompaña la firma del primer contrato",
                "2 referidos de servicios con contrato activo",
                null, now.minusWeeks(1)),
            mission(ambassadorId,
                "Genera Bs 5.000 en comisiones",
                "Alcanza Bs 5.000 acumulados en comisiones confirmadas para subir al tier Oro.",
                "Ascenso a tier Oro + acceso a panel avanzado de métricas",
                "servicios", "alta", "disponible",
                "Revisa tu progreso en el dashboard|Enfócate en referidos de alto impacto|Mantén actividad constante",
                "Bs 5.000 en comisiones confirmadas acumuladas",
                null, now.minusDays(5))
        );
        missionRepo.saveAll(missions);
    }

    private AmbassadorMissionJpaEntity mission(
            UUID ambassadorId, String title, String description, String benefit,
            String type, String priority, String status,
            String steps, String criteria, BigDecimal progress, OffsetDateTime createdAt) {
        AmbassadorMissionJpaEntity e = new AmbassadorMissionJpaEntity();
        e.setId(UUID.randomUUID());
        e.setAmbassadorId(ambassadorId);
        e.setTitle(title);
        e.setDescription(description);
        e.setBenefit(benefit);
        e.setMissionType(type);
        e.setPriority(priority);
        e.setStatus(status);
        e.setSteps(steps);
        e.setCompletionCriteria(criteria);
        e.setProgress(progress);
        e.setCreatedAt(createdAt);
        e.setUpdatedAt(createdAt);
        return e;
    }
}
