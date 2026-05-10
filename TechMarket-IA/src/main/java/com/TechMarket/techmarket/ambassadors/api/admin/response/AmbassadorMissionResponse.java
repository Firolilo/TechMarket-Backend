package com.techmarket.techmarket.ambassadors.api.admin.response;

import java.util.List;

public record AmbassadorMissionResponse(
        String id,
        String title,
        String description,
        String benefit,
        String type,
        String priority,
        String status,
        List<String> steps,
        String completionCriteria,
        Double progress) {}
