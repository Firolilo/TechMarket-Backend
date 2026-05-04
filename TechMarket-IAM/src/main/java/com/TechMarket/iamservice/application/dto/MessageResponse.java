package com.techmarket.iamservice.application.dto;

import java.time.Instant;

public record MessageResponse(String mensaje, Instant timestamp) {}
