package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface SpecialistReviewProjection {

    UUID getId();

    String getCustomerFirstName();

    String getCustomerLastName();

    BigDecimal getRating();

    String getComment();

    String getTechnicianResponse();

    String getServiceName();

    OffsetDateTime getCreatedAt();
}
