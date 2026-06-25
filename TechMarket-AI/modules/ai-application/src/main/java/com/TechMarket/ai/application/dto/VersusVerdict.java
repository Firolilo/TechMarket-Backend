package com.techmarket.ai.application.dto;

import java.util.List;

/**
 * AI verdict for a "Versus" comparison of two marketplace listings: which one is the better deal
 * and why. Field names match the frontend contract consumed by the client Versus page.
 *
 * @param ganadorId id of the product the AI considers the better deal (must be one of the inputs)
 * @param veredicto 1-2 sentence final recommendation with the main reason
 * @param resumen short headline-style summary
 * @param productos per-product evaluation (pros/cons + dimension scores)
 */
public record VersusVerdict(
        String ganadorId, String veredicto, String resumen, List<ProductoEvaluado> productos) {

    /**
     * Per-product evaluation. Scores are integers 0-100 (higher is better); the product with the
     * highest {@code scoreValor} must match {@link VersusVerdict#ganadorId()}.
     */
    public record ProductoEvaluado(
            String id,
            String nombre,
            List<String> pros,
            List<String> contras,
            Integer scorePrecio,
            Integer scoreCalidad,
            Integer scoreReputacion,
            Integer scoreValor) {}
}
