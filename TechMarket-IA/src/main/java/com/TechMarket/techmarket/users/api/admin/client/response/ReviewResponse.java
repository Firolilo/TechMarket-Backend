package com.techmarket.techmarket.users.api.admin.client.response;

import java.time.LocalDate;

public record ReviewResponse(
        String id, Integer calificacion, String comentario, LocalDate fecha) {}
