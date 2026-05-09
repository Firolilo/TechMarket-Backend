package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class SpecialistReviewJpaEntity {

    @Id private UUID id;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "rating")
    private BigDecimal rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "technician_response")
    private String technicianResponse;

    @Column(name = "technician_response_at")
    private OffsetDateTime technicianResponseAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTechnicianResponse() {
        return technicianResponse;
    }

    public void setTechnicianResponse(String technicianResponse) {
        this.technicianResponse = technicianResponse;
    }

    public OffsetDateTime getTechnicianResponseAt() {
        return technicianResponseAt;
    }

    public void setTechnicianResponseAt(OffsetDateTime technicianResponseAt) {
        this.technicianResponseAt = technicianResponseAt;
    }
}
