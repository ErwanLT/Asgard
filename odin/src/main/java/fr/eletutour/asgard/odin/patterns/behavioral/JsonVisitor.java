package fr.eletutour.asgard.odin.patterns.behavioral;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class JsonVisitor implements Visitor<Object> {
    private final StringBuilder jsonBuilder;
    private int indentLevel;

    public JsonVisitor() {
        this.jsonBuilder = new StringBuilder();
        this.indentLevel = 0;
    }

    @Override
    public void visit(Object object) {
        if (object == null) {
            jsonBuilder.append("null");
            return;
        }

        if (object instanceof String) {
            jsonBuilder.append("\"").append(escapeString((String) object)).append("\"");
            return;
        }

        if (object instanceof Number || object instanceof Boolean) {
            jsonBuilder.append(object);
            return;
        }

        if (object instanceof Collection) {
            visitCollection((Collection<?>) object);
            return;
        }

        if (object instanceof Map) {
            visitMap((Map<?, ?>) object);
            return;
        }

        visitObject(object);
    }

    private void visitObject(Object object) {
        jsonBuilder.append("{\n");
        indentLevel++;

        Field[] fields = object.getClass().getDeclaredFields();
        boolean first = true;

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (!first) {
                    jsonBuilder.append(",\n");
                }
                addIndent();
                jsonBuilder.append("\"").append(field.getName()).append("\": ");
                visit(value);
                first = false;
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erreur lors de l'accès au champ " + field.getName(), e);
            }
        }

        jsonBuilder.append("\n");
        indentLevel--;
        addIndent();
        jsonBuilder.append("}");
    }

    private void visitCollection(Collection<?> collection) {
        jsonBuilder.append("[\n");
        indentLevel++;

        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                jsonBuilder.append(",\n");
            }
            addIndent();
            visit(item);
            first = false;
        }

        jsonBuilder.append("\n");
        indentLevel--;
        addIndent();
        jsonBuilder.append("]");
    }

    private void visitMap(Map<?, ?> map) {
        jsonBuilder.append("{\n");
        indentLevel++;

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                jsonBuilder.append(",\n");
            }
            addIndent();
            jsonBuilder.append("\"").append(entry.getKey()).append("\": ");
            visit(entry.getValue());
            first = false;
        }

        jsonBuilder.append("\n");
        indentLevel--;
        addIndent();
        jsonBuilder.append("}");
    }

    private void addIndent() {
        jsonBuilder.append("  ".repeat(indentLevel));
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }

    public String getJson() {
        return jsonBuilder.toString();
    }
} 