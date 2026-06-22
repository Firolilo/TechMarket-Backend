package com.techmarket.ai.application.dto;

/**
 * A semantic-search result: a marketplace offering matched by meaning, with its similarity {@code
 * score} (higher = closer).
 */
public record MarketplaceHitDto(
        String id, String type, String title, String ownerName, double score) {}
