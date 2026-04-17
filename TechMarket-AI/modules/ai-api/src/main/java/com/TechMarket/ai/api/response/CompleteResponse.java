package com.techmarket.ai.api.response;

/** API response DTO for /complete. */
public record CompleteResponse(String content, String model, int tokensUsed) {}
