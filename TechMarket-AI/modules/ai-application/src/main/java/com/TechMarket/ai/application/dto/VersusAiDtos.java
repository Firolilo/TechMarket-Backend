package com.techmarket.ai.application.dto;

import java.util.List;
import java.util.Map;

/** Request DTOs for the marketplace "Versus" AI deal comparator. */
public final class VersusAiDtos {

    private VersusAiDtos() {}

    /**
     * Compare two (or more) marketplace listings to decide the better deal. {@code context} carries
     * any extra real-data grounding assembled by the frontend from TechMarket-IA.
     */
    public record Compare(List<Producto> productos, Map<String, Object> context) {}

    /**
     * A marketplace listing to compare. Optional fields may be {@code null} when TechMarket-IA does
     * not have the data; the AI is instructed to treat missing values as uncertainty, not to invent
     * them.
     */
    public record Producto(
            String id,
            String nombre,
            Double precio,
            String descripcion,
            Double calificacion,
            Integer totalResenas,
            Double reputacionVendedor,
            Integer stock,
            String empresa) {}
}
