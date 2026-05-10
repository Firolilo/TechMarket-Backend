package com.techmarket.techmarket.specialists.api.admin.response;

import java.time.OffsetDateTime;

public record SpecialistChatMessageResponse(
        String id, String remitente, String contenido, OffsetDateTime fecha, String tipo) {}
