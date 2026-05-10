package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ambassador_leads")
public class AmbassadorLeadJpaEntity {

    @Id private UUID id;

    @Column(name = "ambassador_id")
    private UUID ambassadorId;

    @Column(name = "name")
    private String name;

    @Column(name = "lead_type")
    private String leadType;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "source")
    private String source;

    @Column(name = "status")
    private String status;

    @Column(name = "close_probability")
    private int closeProbability;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAmbassadorId() { return ambassadorId; }
    public void setAmbassadorId(UUID ambassadorId) { this.ambassadorId = ambassadorId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLeadType() { return leadType; }
    public void setLeadType(String leadType) { this.leadType = leadType; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCloseProbability() { return closeProbability; }
    public void setCloseProbability(int closeProbability) { this.closeProbability = closeProbability; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
