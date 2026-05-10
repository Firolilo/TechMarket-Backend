package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ambassador_withdrawals")
public class AmbassadorWithdrawalJpaEntity {

    @Id private UUID id;

    @Column(name = "ambassador_id")
    private UUID ambassadorId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "payout_method_id")
    private String payoutMethodId;

    @Column(name = "status")
    private String status;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "estimated_at")
    private LocalDate estimatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAmbassadorId() { return ambassadorId; }
    public void setAmbassadorId(UUID ambassadorId) { this.ambassadorId = ambassadorId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getPayoutMethodId() { return payoutMethodId; }
    public void setPayoutMethodId(String payoutMethodId) { this.payoutMethodId = payoutMethodId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(OffsetDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDate getEstimatedAt() { return estimatedAt; }
    public void setEstimatedAt(LocalDate estimatedAt) { this.estimatedAt = estimatedAt; }
}
