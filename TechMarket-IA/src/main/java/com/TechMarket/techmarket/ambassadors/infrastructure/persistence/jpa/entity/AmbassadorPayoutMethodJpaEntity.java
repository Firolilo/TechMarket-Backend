package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ambassador_payout_methods")
public class AmbassadorPayoutMethodJpaEntity {

    @Id private UUID id;

    @Column(name = "ambassador_id")
    private UUID ambassadorId;

    @Column(name = "method_type")
    private String methodType;

    @Column(name = "bank")
    private String bank;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "holder_name")
    private String holderName;

    @Column(name = "last4")
    private String last4;

    @Column(name = "is_default")
    private boolean defaultMethod;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAmbassadorId() { return ambassadorId; }
    public void setAmbassadorId(UUID ambassadorId) { this.ambassadorId = ambassadorId; }
    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }
    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }
    public boolean isDefaultMethod() { return defaultMethod; }
    public void setDefaultMethod(boolean defaultMethod) { this.defaultMethod = defaultMethod; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
