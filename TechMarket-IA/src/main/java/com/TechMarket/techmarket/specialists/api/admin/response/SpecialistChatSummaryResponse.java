package com.techmarket.techmarket.specialists.api.admin.response;

import java.time.OffsetDateTime;

public record SpecialistChatSummaryResponse(
        String id,
        String cliente,
        String ultimoMensaje,
        int noLeidos,
        OffsetDateTime ultimaActividad) {}
