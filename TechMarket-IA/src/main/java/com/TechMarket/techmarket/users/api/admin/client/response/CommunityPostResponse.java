package com.techmarket.techmarket.users.api.admin.client.response;

public record CommunityPostResponse(
        String id,
        String autor,
        String titulo,
        String contenido,
        String creadoEn,
        long likes,
        long comentarios,
        boolean meGusta) {}
