package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ambassador_onboarding_milestones")
public class AmbassadorOnboardingMilestoneJpaEntity {

    @Id private UUID id;

    @Column(name = "ambassador_referral_id")
    private UUID ambassadorReferralId;

    @Column(name = "milestone_code")
    private String milestoneCode;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAmbassadorReferralId() { return ambassadorReferralId; }
    public void setAmbassadorReferralId(UUID ambassadorReferralId) { this.ambassadorReferralId = ambassadorReferralId; }
    public String getMilestoneCode() { return milestoneCode; }
    public void setMilestoneCode(String milestoneCode) { this.milestoneCode = milestoneCode; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }
}
