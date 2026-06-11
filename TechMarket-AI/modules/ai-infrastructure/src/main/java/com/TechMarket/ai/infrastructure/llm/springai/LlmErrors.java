package com.techmarket.ai.infrastructure.llm.springai;

import com.techmarket.ai.application.exception.LlmUnavailableException;
import java.util.Locale;

/** Translates Spring AI / provider exceptions into clean application-level exceptions. */
final class LlmErrors {

    private LlmErrors() {}

    /**
     * Map a provider failure to a {@link LlmUnavailableException} when it is a rate-limit/quota
     * error (HTTP 429 / RESOURCE_EXHAUSTED); otherwise return the original exception to rethrow.
     */
    static RuntimeException translate(RuntimeException ex) {
        String message = String.valueOf(ex.getMessage()).toLowerCase(Locale.ROOT);
        boolean rateLimited =
                message.contains("429")
                        || message.contains("resource_exhausted")
                        || message.contains("quota")
                        || message.contains("rate limit");
        if (rateLimited) {
            return new LlmUnavailableException(
                    "El proveedor de IA (Gemini, free tier) alcanzó su límite de cuota."
                            + " Intenta de nuevo más tarde.",
                    true);
        }
        return ex;
    }
}
