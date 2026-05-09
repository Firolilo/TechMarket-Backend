package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistCalendarEntryResponse(
        String id,
        String cliente,
        String servicio,
        String fecha,
        String hora,
        String estado,
        String modalidad) {}
