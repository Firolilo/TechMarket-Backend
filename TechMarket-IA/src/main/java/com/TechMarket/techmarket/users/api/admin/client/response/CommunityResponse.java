package com.techmarket.techmarket.users.api.admin.client.response;

public record CommunityResponse(
        String id,
        String nombre,
        String descripcion,
        int miembros,
        boolean unido,
        String creadoEn) {}
