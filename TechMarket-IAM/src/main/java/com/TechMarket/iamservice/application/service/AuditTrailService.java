package com.techmarket.iamservice.application.service;

import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditTrailService {

    private final JdbcTemplate jdbcTemplate;

    public AuditTrailService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void record(
            String action, String entity, String entityId, String tenantId, String actorUserId) {
        if (tenantId == null || tenantId.isBlank()) {
            return;
        }

        String safeAction = action == null || action.isBlank() ? "UNKNOWN_ACTION" : action;
        String safeEntity = entity == null || entity.isBlank() ? "UNKNOWN_ENTITY" : entity;
        String safeEntityId = entityId == null || entityId.isBlank() ? "N/A" : entityId;
        String safeActor = actorUserId == null || actorUserId.isBlank() ? "system" : actorUserId;
        Timestamp now = Timestamp.from(Instant.now());

        jdbcTemplate.update(
                """
                INSERT INTO audit_log (
                    tenant_id,
                    action,
                    entity,
                    entity_id,
                    user_id,
                    occurred_at,
                    event_type,
                    entity_type,
                    details
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                tenantId,
                safeAction,
                safeEntity,
                safeEntityId,
                safeActor,
                now,
                safeAction,
                safeEntity,
                "{}");
    }
}
