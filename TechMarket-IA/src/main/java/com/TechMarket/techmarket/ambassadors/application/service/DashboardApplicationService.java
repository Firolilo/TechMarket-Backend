package com.techmarket.techmarket.ambassadors.application.service;

import com.techmarket.techmarket.ambassadors.api.mobile.response.ActivityItemResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.DailyActivityResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.DashboardStatsResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.DashboardStatsResponse.ActivityByTypeResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.DashboardStatsResponse.LevelImpactResponse;
import com.techmarket.techmarket.ambassadors.api.mobile.response.ReferredBusinessResponse;
import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralSpringDataRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DashboardApplicationService {

    private static final double LEVEL1_PCT = 5.0;
    private static final double LEVEL2_PCT = 3.0;
    private static final double LEVEL3_PCT = 2.0;

    private final AmbassadorRepositoryPort ambassadorRepo;
    private final AmbassadorReferralSpringDataRepository referralRepo;
    private final AmbassadorCommissionSpringDataRepository commissionRepo;

    public DashboardApplicationService(
            AmbassadorRepositoryPort ambassadorRepo,
            AmbassadorReferralSpringDataRepository referralRepo,
            AmbassadorCommissionSpringDataRepository commissionRepo) {
        this.ambassadorRepo = ambassadorRepo;
        this.referralRepo = referralRepo;
        this.commissionRepo = commissionRepo;
    }

    public DashboardStatsResponse getStats(UUID userId) {
        Ambassador ambassador = findAmbassador(userId);
        List<AmbassadorReferralJpaEntity> referrals =
                referralRepo.findByAmbassadorId(ambassador.id());
        List<AmbassadorCommissionJpaEntity> commissions =
                commissionRepo.findByAmbassadorId(ambassador.id());

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfMonth =
                now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime startOfLastMonth = startOfMonth.minusMonths(1);

        double thisMonthTotal = sumCommissions(commissions, startOfMonth, now);
        double lastMonthTotal = sumCommissions(commissions, startOfLastMonth, startOfMonth);
        double totalAllTime = sumAllCommissions(commissions);

        double variationPercent =
                lastMonthTotal == 0
                        ? (thisMonthTotal > 0 ? 100.0 : 0.0)
                        : ((thisMonthTotal - lastMonthTotal) / lastMonthTotal) * 100.0;

        boolean allConfirmed =
                !commissions.isEmpty()
                        && commissions.stream()
                                .allMatch(
                                        c ->
                                                "CONFIRMED".equalsIgnoreCase(c.getStatus())
                                                        || "PAID".equalsIgnoreCase(c.getStatus()));

        long activeCount =
                referrals.stream().filter(r -> "ACTIVE".equalsIgnoreCase(r.getStatus())).count();
        String activityState;
        if (activeCount == 0) activityState = "SIN_ACTIVIDAD";
        else if (activeCount <= 2) activityState = "ACTIVIDAD_INICIAL";
        else activityState = "ACTIVIDAD_CONSTANTE";

        int hardware =
                (int)
                        referrals.stream()
                                .filter(
                                        r ->
                                                "ACTIVE".equalsIgnoreCase(r.getStatus())
                                                        && "HARDWARE"
                                                                .equalsIgnoreCase(
                                                                        r.getReferralType()))
                                .count();
        int software =
                (int)
                        referrals.stream()
                                .filter(
                                        r ->
                                                "ACTIVE".equalsIgnoreCase(r.getStatus())
                                                        && "SOFTWARE"
                                                                .equalsIgnoreCase(
                                                                        r.getReferralType()))
                                .count();
        int services =
                (int)
                        referrals.stream()
                                .filter(
                                        r ->
                                                "ACTIVE".equalsIgnoreCase(r.getStatus())
                                                        && ("SERVICES"
                                                                        .equalsIgnoreCase(
                                                                                r.getReferralType())
                                                                || "SERVICIOS"
                                                                        .equalsIgnoreCase(
                                                                                r
                                                                                        .getReferralType())))
                                .count();

        // Level breakdown from actual attributionType on each commission
        double l1Income = incomeByLevel(commissions, 1);
        double l2Income = incomeByLevel(commissions, 2);
        double l3Income = incomeByLevel(commissions, 3);
        double l1Impact = l1Income > 0 ? l1Income / (LEVEL1_PCT / 100.0) : 0.0;
        double l2Impact = l2Income > 0 ? l2Income / (LEVEL2_PCT / 100.0) : 0.0;
        double l3Impact = l3Income > 0 ? l3Income / (LEVEL3_PCT / 100.0) : 0.0;
        List<LevelImpactResponse> levelBreakdown =
                List.of(
                        new LevelImpactResponse(1, l1Impact, LEVEL1_PCT, l1Income),
                        new LevelImpactResponse(2, l2Impact, LEVEL2_PCT, l2Income),
                        new LevelImpactResponse(3, l3Impact, LEVEL3_PCT, l3Income));

        double totalImpact = l1Impact + l2Impact + l3Impact;
        return new DashboardStatsResponse(
                totalImpact,
                totalAllTime,
                allConfirmed,
                new ActivityByTypeResponse(hardware, software, services),
                Math.round(variationPercent * 10.0) / 10.0,
                activityState,
                levelBreakdown);
    }

    public List<ActivityItemResponse> getRecentActivity(UUID userId, int limit) {
        Ambassador ambassador = findAmbassador(userId);
        List<AmbassadorCommissionJpaEntity> commissions =
                commissionRepo.findAllByAmbassadorIdOrderByGeneratedAtDesc(ambassador.id());
        List<AmbassadorReferralJpaEntity> referrals =
                referralRepo.findByAmbassadorId(ambassador.id());

        Map<UUID, String> referralNames =
                referrals.stream()
                        .collect(
                                Collectors.toMap(
                                        AmbassadorReferralJpaEntity::getId,
                                        r -> r.getName() != null ? r.getName() : "Empresa"));
        Map<UUID, String> referralTypes =
                referrals.stream()
                        .collect(
                                Collectors.toMap(
                                        AmbassadorReferralJpaEntity::getId,
                                        r ->
                                                r.getReferralType() != null
                                                        ? r.getReferralType()
                                                        : "SERVICES"));

        return commissions.stream()
                .limit(limit)
                .map(
                        c -> {
                            String name =
                                    c.getAmbassadorReferralId() != null
                                            ? referralNames.getOrDefault(
                                                    c.getAmbassadorReferralId(), "Referido")
                                            : "Referido";
                            String refType =
                                    c.getAmbassadorReferralId() != null
                                            ? referralTypes.getOrDefault(
                                                    c.getAmbassadorReferralId(), "SERVICES")
                                            : "SERVICES";
                            double amount = parseAmount(c.getAmount());
                            int lvl = levelFrom(c.getAttributionType());
                            return new ActivityItemResponse(
                                    labelForEvent(c.getEventType()),
                                    name,
                                    c.getGeneratedAt(),
                                    amount > 0 ? amount : null,
                                    refType,
                                    lvl);
                        })
                .toList();
    }

    public List<DailyActivityResponse> getWeeklyActivity(UUID userId, String periodo) {
        Ambassador ambassador = findAmbassador(userId);
        List<AmbassadorCommissionJpaEntity> commissions =
                commissionRepo.findByAmbassadorId(ambassador.id());
        OffsetDateTime now = OffsetDateTime.now();
        return switch (periodo == null ? "semana" : periodo.toLowerCase()) {
            case "4semanas" -> buildWeekPoints(commissions, now, 4);
            case "mes" -> buildWeekPoints(commissions, now, 5);
            case "trimestre" -> buildMonthPoints(commissions, now, 3);
            default -> buildDailyPoints(commissions, now, 7);
        };
    }

    private List<DailyActivityResponse> buildDailyPoints(
            List<AmbassadorCommissionJpaEntity> commissions, OffsetDateTime now, int days) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<DailyActivityResponse> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = now.toLocalDate().minusDays(i);
            OffsetDateTime start = day.atStartOfDay(now.getOffset()).toOffsetDateTime();
            OffsetDateTime end = start.plusDays(1);
            double income = sumCommissions(commissions, start, end);
            double impact = income > 0 ? income / (LEVEL1_PCT / 100.0) : 0.0;
            result.add(new DailyActivityResponse(day.format(fmt), impact, income));
        }
        return result;
    }

    private List<DailyActivityResponse> buildWeekPoints(
            List<AmbassadorCommissionJpaEntity> commissions, OffsetDateTime now, int weeks) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        List<DailyActivityResponse> result = new ArrayList<>();
        for (int i = weeks - 1; i >= 0; i--) {
            LocalDate weekEnd = now.toLocalDate().minusWeeks(i);
            LocalDate weekStart = weekEnd.minusDays(6);
            OffsetDateTime start = weekStart.atStartOfDay(now.getOffset()).toOffsetDateTime();
            OffsetDateTime end =
                    weekEnd.plusDays(1).atStartOfDay(now.getOffset()).toOffsetDateTime();
            double income = sumCommissions(commissions, start, end);
            double impact = income > 0 ? income / (LEVEL1_PCT / 100.0) : 0.0;
            result.add(new DailyActivityResponse(weekStart.format(fmt), impact, income));
        }
        return result;
    }

    private List<DailyActivityResponse> buildMonthPoints(
            List<AmbassadorCommissionJpaEntity> commissions, OffsetDateTime now, int months) {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("es"));
        List<DailyActivityResponse> result = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            OffsetDateTime monthStart =
                    now.minusMonths(i)
                            .withDayOfMonth(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);
            OffsetDateTime monthEnd = monthStart.plusMonths(1);
            double income = sumCommissions(commissions, monthStart, monthEnd);
            double impact = income > 0 ? income / (LEVEL1_PCT / 100.0) : 0.0;
            result.add(new DailyActivityResponse(monthStart.format(fmt), impact, income));
        }
        return result;
    }

    private int levelFrom(String attributionType) {
        if (attributionType == null) return 1;
        String upper = attributionType.toUpperCase();
        if (upper.contains("3")) return 3;
        if (upper.contains("2")) return 2;
        return 1;
    }

    private double incomeByLevel(List<AmbassadorCommissionJpaEntity> commissions, int level) {
        return commissions.stream()
                .filter(c -> levelFrom(c.getAttributionType()) == level)
                .mapToDouble(c -> parseAmount(c.getAmount()))
                .sum();
    }

    public List<ReferredBusinessResponse> getReferredBusinesses(UUID userId) {
        Ambassador ambassador = findAmbassador(userId);
        List<AmbassadorReferralJpaEntity> referrals =
                referralRepo.findByAmbassadorId(ambassador.id());
        List<AmbassadorCommissionJpaEntity> commissions =
                commissionRepo.findByAmbassadorId(ambassador.id());

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfMonth =
                now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        Map<UUID, List<AmbassadorCommissionJpaEntity>> byReferral =
                commissions.stream()
                        .filter(c -> c.getAmbassadorReferralId() != null)
                        .collect(
                                Collectors.groupingBy(
                                        AmbassadorCommissionJpaEntity::getAmbassadorReferralId));

        return referrals.stream()
                .sorted(
                        Comparator.comparing(
                                AmbassadorReferralJpaEntity::getCreatedAt,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .map(
                        r -> {
                            List<AmbassadorCommissionJpaEntity> rc =
                                    byReferral.getOrDefault(r.getId(), List.of());
                            double totalIncome =
                                    rc.stream().mapToDouble(c -> parseAmount(c.getAmount())).sum();
                            double monthlyIncome =
                                    rc.stream()
                                            .filter(
                                                    c ->
                                                            c.getGeneratedAt() != null
                                                                    && !c.getGeneratedAt()
                                                                            .isBefore(startOfMonth))
                                            .mapToDouble(c -> parseAmount(c.getAmount()))
                                            .sum();
                            double totalImpact = totalIncome / (LEVEL1_PCT / 100.0);
                            double monthlyImpact = monthlyIncome / (LEVEL1_PCT / 100.0);

                            return new ReferredBusinessResponse(
                                    r.getId(),
                                    r.getName() != null ? r.getName() : "Sin nombre",
                                    r.getCity() != null ? r.getCity() : "-",
                                    mapType(r.getReferralType()),
                                    mapStatus(r.getStatus()),
                                    monthlyImpact,
                                    monthlyIncome,
                                    totalImpact,
                                    totalIncome,
                                    1,
                                    r.getCreatedAt(),
                                    r.getLastActivityAt());
                        })
                .toList();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Ambassador findAmbassador(UUID userId) {
        return ambassadorRepo
                .findByUserId(userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Ambassador not found"));
    }

    private double sumCommissions(
            List<AmbassadorCommissionJpaEntity> commissions,
            OffsetDateTime from,
            OffsetDateTime to) {
        return commissions.stream()
                .filter(
                        c ->
                                c.getGeneratedAt() != null
                                        && !c.getGeneratedAt().isBefore(from)
                                        && c.getGeneratedAt().isBefore(to))
                .mapToDouble(c -> parseAmount(c.getAmount()))
                .sum();
    }

    private double sumAllCommissions(List<AmbassadorCommissionJpaEntity> commissions) {
        return commissions.stream().mapToDouble(c -> parseAmount(c.getAmount())).sum();
    }

    private double parseAmount(String amount) {
        if (amount == null || amount.isBlank()) return 0.0;
        try {
            return new BigDecimal(amount.replace("Bs", "").replace(",", "").trim()).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String mapType(String referralType) {
        if (referralType == null) return "servicios";
        return switch (referralType.toUpperCase()) {
            case "HARDWARE" -> "hardware";
            case "SOFTWARE" -> "software";
            case "MIXED", "MIXTO" -> "mixto";
            default -> "servicios";
        };
    }

    private String mapStatus(String status) {
        if (status == null) return "inactivo";
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> "activo";
            case "PAUSED", "SUSPENDED" -> "pausado";
            default -> "inactivo";
        };
    }

    private String labelForEvent(String eventType) {
        if (eventType == null) return "Comisión generada";
        return switch (eventType.toUpperCase()) {
            case "SUBSCRIPTION" -> "Nueva suscripción";
            case "RENEWAL" -> "Renovación";
            case "REFERRAL" -> "Nuevo referido";
            case "ACTIVATION" -> "Activación";
            default -> "Comisión generada";
        };
    }
}
