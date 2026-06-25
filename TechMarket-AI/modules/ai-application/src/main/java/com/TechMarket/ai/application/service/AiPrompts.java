package com.techmarket.ai.application.service;

import java.util.Collection;
import java.util.Map;

/** Small helpers for building LLM user prompts from optional business context. */
final class AiPrompts {

    private AiPrompts() {}

    /** Render optional context for a prompt, or a placeholder when none was provided. */
    static String context(Object context) {
        if (context == null) {
            return "(sin contexto adicional; usa supuestos razonables del dominio)";
        }
        // Cuando llega contexto estructurado (Map/Collection del JSON del frontend) lo
        // renderizamos como texto legible e indentado para que el modelo lo lea con
        // precisión, en lugar del toString() crudo de Java.
        if (context instanceof Map<?, ?> || context instanceof Collection<?>) {
            StringBuilder sb = new StringBuilder();
            render(sb, context, 0);
            String rendered = sb.toString().trim();
            return rendered.isEmpty() ? "(sin contexto adicional)" : rendered;
        }
        String text = String.valueOf(context).trim();
        return text.isEmpty() ? "(sin contexto adicional)" : text;
    }

    private static void render(StringBuilder sb, Object value, int depth) {
        String indent = "  ".repeat(depth);

        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object child = entry.getValue();
                if (isScalar(child)) {
                    if (isEmptyScalar(child)) {
                        continue;
                    }
                    sb.append(indent)
                            .append("- ")
                            .append(entry.getKey())
                            .append(": ")
                            .append(child)
                            .append('\n');
                } else if (isEmptyContainer(child)) {
                    // Omite secciones vacías para no introducir ruido en el prompt.
                } else {
                    sb.append(indent).append("- ").append(entry.getKey()).append(":\n");
                    render(sb, child, depth + 1);
                }
            }
        } else if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (isScalar(item)) {
                    if (isEmptyScalar(item)) {
                        continue;
                    }
                    sb.append(indent).append("- ").append(item).append('\n');
                } else {
                    sb.append(indent).append("-\n");
                    render(sb, item, depth + 1);
                }
            }
        } else {
            sb.append(indent).append(String.valueOf(value)).append('\n');
        }
    }

    private static boolean isScalar(Object value) {
        return value == null
                || value instanceof String
                || value instanceof Number
                || value instanceof Boolean;
    }

    private static boolean isEmptyScalar(Object value) {
        return value == null || (value instanceof String s && s.isBlank());
    }

    private static boolean isEmptyContainer(Object value) {
        return (value instanceof Map<?, ?> m && m.isEmpty())
                || (value instanceof Collection<?> c && c.isEmpty());
    }
}
