package com.techmarket.ai.application.service;

import com.techmarket.ai.application.dto.VersusAiDtos;
import com.techmarket.ai.application.dto.VersusVerdict;
import com.techmarket.ai.application.port.in.VersusAiUseCase;
import com.techmarket.ai.application.port.out.AuditPort;
import com.techmarket.ai.application.port.out.StructuredLlmPort;
import java.util.List;
import java.util.Map;

/** Marketplace "Versus" deal comparator backed by a structured LLM. */
public class VersusAiService implements VersusAiUseCase {

    private static final String SYSTEM =
            """
            Eres un asesor de compras de TechMarket (marketplace de tecnología). El usuario te da DOS \
            publicaciones del marketplace y debes decidir cuál es el MEJOR DEAL (mejor relación \
            valor/precio) y explicar por qué, de forma honesta y útil para el comprador.

            Reglas:
            - Responde SIEMPRE en español, con tono claro y directo.
            - Básate SOLO en los datos provistos de cada producto (precio, descripción, calificación \
            y número de reseñas, reputación del vendedor, stock). Si falta un dato, NO lo inventes: \
            trátalo como incertidumbre y dilo en 'contras' o en el 'veredicto'.
            - 'ganadorId' DEBE ser exactamente el id de uno de los productos provistos (el mejor \
            deal). Si están realmente empatados, elige el de mejor relación calificación/precio y \
            explícalo en el veredicto.
            - Para CADA producto completa: 'pros' (2 a 3), 'contras' (1 a 3) y cuatro puntajes \
            enteros de 0 a 100: 'scorePrecio' (qué tan conveniente es el precio frente al otro), \
            'scoreCalidad' (calificación y número de reseñas), 'scoreReputacion' (reputación del \
            vendedor) y 'scoreValor' (valor global como deal).
            - El producto con mayor 'scoreValor' DEBE coincidir con 'ganadorId'.
            - 'veredicto' = 1 a 2 frases con la recomendación final y el motivo principal. \
            'resumen' = una sola frase corta para mostrar como titular.
            - No incluyas nada fuera de comparar estas publicaciones.""";

    private final StructuredLlmPort llm;
    private final AuditPort audit;

    public VersusAiService(StructuredLlmPort llm, AuditPort audit) {
        this.llm = llm;
        this.audit = audit;
    }

    @Override
    public VersusVerdict compare(VersusAiDtos.Compare command) {
        String user = buildUserPrompt(command);
        VersusVerdict result = llm.generate(SYSTEM, user, VersusVerdict.class);
        audit.audit("marketplace.versus.compare", Map.of("productos", productIds(command)));
        return result;
    }

    private static String buildUserPrompt(VersusAiDtos.Compare command) {
        List<VersusAiDtos.Producto> productos =
                command.productos() == null ? List.of() : command.productos();
        StringBuilder sb = new StringBuilder();
        sb.append("Compara estas publicaciones del marketplace y dime cuál es el mejor deal.\n");
        int i = 1;
        for (VersusAiDtos.Producto p : productos) {
            sb.append("\nProducto ").append(i++).append(":\n").append(describe(p));
        }
        sb.append("\nContexto adicional:\n").append(AiPrompts.context(command.context()));
        return sb.toString();
    }

    private static String describe(VersusAiDtos.Producto p) {
        return "- id: "
                + value(p.id())
                + "\n- nombre: "
                + value(p.nombre())
                + "\n- precio: "
                + value(p.precio())
                + "\n- calificación: "
                + value(p.calificacion())
                + " ("
                + value(p.totalResenas())
                + " reseñas)"
                + "\n- reputación del vendedor: "
                + value(p.reputacionVendedor())
                + "\n- vendedor: "
                + value(p.empresa())
                + "\n- stock: "
                + value(p.stock())
                + "\n- descripción: "
                + value(p.descripcion())
                + "\n";
    }

    private static String value(Object v) {
        if (v == null) {
            return "no disponible";
        }
        String text = String.valueOf(v).trim();
        return text.isEmpty() ? "no disponible" : text;
    }

    private static List<String> productIds(VersusAiDtos.Compare command) {
        if (command.productos() == null) {
            return List.of();
        }
        return command.productos().stream()
                .map(VersusAiDtos.Producto::id)
                .map(VersusAiService::value)
                .toList();
    }
}
