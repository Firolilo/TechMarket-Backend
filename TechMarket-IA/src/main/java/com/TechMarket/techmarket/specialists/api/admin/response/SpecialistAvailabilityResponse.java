package com.techmarket.techmarket.specialists.api.admin.response;

import java.util.List;

public record SpecialistAvailabilityResponse(
        String estado,
        List<String> dias,
        SpecialistScheduleResponse horario,
        List<String> modalidad,
        String cobertura) {}
