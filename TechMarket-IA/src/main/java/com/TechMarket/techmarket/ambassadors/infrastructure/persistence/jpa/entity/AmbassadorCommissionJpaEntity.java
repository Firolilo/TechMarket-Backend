package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ambassador_commissions")
public class AmbassadorCommissionJpaEntity {

    @Id private UUID id;

    @Column(name = "ambassador_id")
    private UUID ambassadorId;

    @Column(name = "ambassador_referral_id")
    private UUID ambassadorReferralId;

    @Column(name = "commission_rule_id")
    private UUID commissionRuleId;

    @Column(name = "attribution_type")
    private String attributionType;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "status")
    private String status;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAmbassadorId() {
        return ambassadorId;
    }

    public void setAmbassadorId(UUID ambassadorId) {
        this.ambassadorId = ambassadorId;
    }

    public UUID getAmbassadorReferralId() {
        return ambassadorReferralId;
    }

    public void setAmbassadorReferralId(UUID ambassadorReferralId) {
        this.ambassadorReferralId = ambassadorReferralId;
    }

    public UUID getCommissionRuleId() {
        return commissionRuleId;
    }

    public void setCommissionRuleId(UUID commissionRuleId) {
        this.commissionRuleId = commissionRuleId;
    }

    public String getAttributionType() {
        return attributionType;
    }

    public void setAttributionType(String attributionType) {
        this.attributionType = attributionType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(OffsetDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
