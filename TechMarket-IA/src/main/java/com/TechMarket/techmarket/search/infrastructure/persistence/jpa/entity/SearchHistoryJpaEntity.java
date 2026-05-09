package com.techmarket.techmarket.search.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "search_history")
public class SearchHistoryJpaEntity {

    @Id private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "query_text")
    private String queryText;

    @Column(name = "result_type")
    private String resultType;

    @Column(name = "searched_at")
    private OffsetDateTime searchedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public OffsetDateTime getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(OffsetDateTime searchedAt) {
        this.searchedAt = searchedAt;
    }
}
