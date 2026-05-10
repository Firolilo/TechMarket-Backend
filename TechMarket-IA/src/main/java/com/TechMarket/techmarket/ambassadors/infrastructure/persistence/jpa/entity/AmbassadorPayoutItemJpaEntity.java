package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "payout_items")
public class AmbassadorPayoutItemJpaEntity {

    @Id private UUID id;

    @Column(name = "payout_cycle_id")
    private UUID payoutCycleId;

    @Column(name = "ambassador_id")
    private UUID ambassadorId;

    @Column(name = "ambassador_commission_id")
    private UUID ambassadorCommissionId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "status")
    private String status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPayoutCycleId() {
        return payoutCycleId;
    }

    public void setPayoutCycleId(UUID payoutCycleId) {
        this.payoutCycleId = payoutCycleId;
    }

    public UUID getAmbassadorId() {
        return ambassadorId;
    }

    public void setAmbassadorId(UUID ambassadorId) {
        this.ambassadorId = ambassadorId;
    }

    public UUID getAmbassadorCommissionId() {
        return ambassadorCommissionId;
    }

    public void setAmbassadorCommissionId(UUID ambassadorCommissionId) {
        this.ambassadorCommissionId = ambassadorCommissionId;
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
}
