package com.techmarket.ai.application.dto;

/**
 * A marketplace catalog item to be indexed for semantic search. {@code content} is the embeddable
 * text (title + description); the rest is metadata returned with a hit.
 *
 * @param id stable id of the listing/service (e.g. "PROD-..." or "SVC-...")
 * @param type "empresa" or "especialista"
 * @param title display title of the offering
 * @param ownerName company or specialist display name
 * @param content text to embed (title + description)
 */
public record MarketplaceDocDto(
        String id, String type, String title, String ownerName, String content) {}
