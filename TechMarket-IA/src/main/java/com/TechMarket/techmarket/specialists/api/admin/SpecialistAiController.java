package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.PricingSuggestionRequest;
import com.techmarket.techmarket.specialists.api.admin.request.SpecialistAiQueryRequest;
import com.techmarket.techmarket.specialists.api.admin.request.SpecialistImprovementPlanRequest;
import com.techmarket.techmarket.specialists.api.admin.response.PricingRangeResponse;
import com.techmarket.techmarket.specialists.api.admin.response.PricingSuggestionDetailResponse;
import com.techmarket.techmarket.specialists.api.admin.response.PricingSuggestionResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAiAnswerResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAiInsightRadarResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAiInsightsResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAiQueryResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistImprovementPlanDetailResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistImprovementPlanResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistScheduleOptimizationResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistAiQueryJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAiQuerySpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specialists/ai")
public class SpecialistAiController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistAiQuerySpringDataRepository aiQueryRepository;

    public SpecialistAiController(
            SpecialistIdentitySupport identitySupport,
            SpecialistAiQuerySpringDataRepository aiQueryRepository) {
        this.identitySupport = identitySupport;
        this.aiQueryRepository = aiQueryRepository;
    }

    @PostMapping("/query")
    public SpecialistAiQueryResponse query(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody SpecialistAiQueryRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistAiAnswerResponse answer =
                new SpecialistAiAnswerResponse(
                        "Tu mejor palanca hoy es ordenar solicitudes por urgencia, zona y margen.",
                        List.of(
                                "Etiqueta solicitudes en critica, importante y seguimiento.",
                                "Agrupa visitas por zona para compactar desplazamientos.",
                                "Prioriza servicios con mejor margen neto y menor tiempo de ejecución."),
                        "disponibilidad");
        SpecialistAiQueryJpaEntity query = new SpecialistAiQueryJpaEntity();
        query.setId(UUID.randomUUID());
        query.setUserId(currentUserId);
        query.setQueryText(request.consulta());
        query.setFocus(answer.foco());
        query.setResponseSummary(answer.resumen());
        query.setCreatedAt(OffsetDateTime.now());
        aiQueryRepository.save(query);
        return new SpecialistAiQueryResponse(request.consulta(), answer);
    }

    @GetMapping("/insights")
    public SpecialistAiInsightsResponse insights(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        identitySupport.requireUserId(userId);
        return new SpecialistAiInsightsResponse(
                List.of(
                        new SpecialistAiInsightRadarResponse("Urgencia de casos", 79),
                        new SpecialistAiInsightRadarResponse("Probabilidad de cierre", 72),
                        new SpecialistAiInsightRadarResponse("Carga operativa", 66),
                        new SpecialistAiInsightRadarResponse("Potencial de reputación", 84)),
                "Momento favorable para captar y responder.",
                "Reforzar portafolio y velocidad de respuesta");
    }

    @PostMapping("/pricing-suggestion")
    public PricingSuggestionResponse pricingSuggestion(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody PricingSuggestionRequest request) {
        identitySupport.requireUserId(userId);
        BigDecimal current = request.precioActual() == null ? new BigDecimal("120.00") : request.precioActual();
        BigDecimal recommended = current.multiply(new BigDecimal("1.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal min = current.multiply(new BigDecimal("0.92")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal max = current.multiply(new BigDecimal("1.25")).setScale(2, RoundingMode.HALF_UP);
        return new PricingSuggestionResponse(
                request.servicio(),
                formatMoney(current),
                new PricingSuggestionDetailResponse(
                        formatMoney(recommended),
                        new PricingRangeResponse(formatMoney(min), formatMoney(max)),
                        "Demanda alta en tu zona y buena reputación reciente."));
    }

    @PostMapping("/improvement-plan")
    public SpecialistImprovementPlanResponse improvementPlan(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody SpecialistImprovementPlanRequest request) {
        identitySupport.requireUserId(userId);
        return new SpecialistImprovementPlanResponse(
                request.area(),
                new SpecialistImprovementPlanDetailResponse(
                        "Mejorar reputación y conversión en servicios clave",
                        List.of(
                                "Responder reseñas en menos de 2 horas.",
                                "Solicitar reseña después de cada servicio completado.",
                                "Ofrecer seguimiento post-servicio."),
                        "30 días"));
    }

    @PostMapping("/schedule-optimization")
    public SpecialistScheduleOptimizationResponse scheduleOptimization(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        identitySupport.requireUserId(userId);
        return new SpecialistScheduleOptimizationResponse(
                "Agrupar visitas por zona puede liberar 1 ventana adicional de atención.",
                List.of(
                        "Confirmar agenda del día siguiente antes de las 18:00.",
                        "Agrupar visitas en zonas norte, sur y centro por día."));
    }

    private String formatMoney(BigDecimal amount) {
        return "Bs " + amount.toPlainString();
    }
}
