package com.techmarket.techmarket.ambassadors.api.mobile.response;

import java.util.List;
import java.util.UUID;

public record MissionResponse(
        UUID id,
        String title,
        String description,
        String benefit,
        String type,
        String priority,
        String status,
        List<String> steps,
        String completionCriteria,
        Double progress) {}
