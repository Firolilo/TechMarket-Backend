package com.techmarket.techmarket.users.api.admin.client.response;

import java.time.OffsetDateTime;

public record ChatMessageResponse(
        String id, String remitente, String contenido, OffsetDateTime fecha) {}
