package com.techmarket.techmarket.ambassadors.api.mobile.response;

import java.time.OffsetDateTime;

public record ActivityItemResponse(
        String title,
        String subtitle,
        OffsetDateTime timestamp,
        Double amount,
        String referralType,
        Integer level) {}
