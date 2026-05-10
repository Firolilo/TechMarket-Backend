package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ambassador_referral_files")
public class AmbassadorReferralFileJpaEntity {

    @Id private UUID id;

    @Column(name = "ambassador_referral_id")
    private UUID ambassadorReferralId;

    @Column(name = "url")
    private String url;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAmbassadorReferralId() { return ambassadorReferralId; }
    public void setAmbassadorReferralId(UUID ambassadorReferralId) { this.ambassadorReferralId = ambassadorReferralId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
