package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import java.math.BigDecimal;

public interface SpecialistReviewStatsProjection {

    Long getTotalReviews();

    BigDecimal getAverageRating();
}
