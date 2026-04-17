package com.techmarket.core.audit.infrastructure.listener;

import com.techmarket.core.audit.domain.event.AuditEvent;
import com.techmarket.core.audit.domain.model.AuditLog;
import com.techmarket.core.audit.infrastructure.repository.AuditLogRepository;
import com.techmarket.core.security.context.SecurityTenantContext;
import com.techmarket.core.security.context.SecurityUserContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Listener de auditoría funcional. */
@Component
public class AuditEventListener {

    private final AuditLogRepository repository;

    public AuditEventListener(AuditLogRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void onAuditEvent(AuditEvent event) {

        AuditLog log =
                new AuditLog(
                        event.action(),
                        event.entity(),
                        event.entityId(),
                        SecurityUserContext.getUserIdentifier(),
                        SecurityTenantContext.getTenantId(),
                        event.occurredAt());

        repository.save(log);
    }
}
