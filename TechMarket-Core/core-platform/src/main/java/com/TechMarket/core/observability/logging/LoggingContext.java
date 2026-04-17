package com.techmarket.core.observability.logging;

import com.techmarket.core.observability.context.CorrelationIdContext;
import org.slf4j.MDC;

public final class LoggingContext {

    private LoggingContext() {}

    public static void enrich() {
        MDC.put("correlationId", CorrelationIdContext.get());
    }

    public static void clear() {
        MDC.clear();
    }
}
