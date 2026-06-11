package com.techmarket.ai.application.exception;

/**
 * Thrown when the LLM provider cannot serve the request (e.g. the Gemini free-tier quota is
 * exhausted). Mapped by the API layer to a clear HTTP status instead of a generic error.
 */
public class LlmUnavailableException extends RuntimeException {

    private final boolean rateLimited;

    public LlmUnavailableException(String message, boolean rateLimited) {
        super(message);
        this.rateLimited = rateLimited;
    }

    public boolean isRateLimited() {
        return rateLimited;
    }
}
