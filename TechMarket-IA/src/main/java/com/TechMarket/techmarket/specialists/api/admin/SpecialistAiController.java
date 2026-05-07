package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.SpecialistAiQueryRequest;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAiAnswerResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAiQueryResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistAiQueryJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAiQuerySpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
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
}
