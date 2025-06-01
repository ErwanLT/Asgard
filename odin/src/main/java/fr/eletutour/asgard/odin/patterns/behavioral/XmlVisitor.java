package fr.eletutour.asgard.odin.patterns.behavioral;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class XmlVisitor implements Visitor<Object> {
    private final StringBuilder xmlBuilder;
    private int indentLevel;
    private String rootElementName;

    public XmlVisitor() {
        this.xmlBuilder = new StringBuilder();
        this.indentLevel = 0;
        this.rootElementName = "root";
    }

    public XmlVisitor(String rootElementName) {
        this();
        this.rootElementName = rootElementName;
    }

    @Override
    public void visit(Object object) {
        if (object == null) {
            xmlBuilder.append("<null/>");
            return;
        }

        if (object instanceof String || object instanceof Number || object instanceof Boolean) {
            xmlBuilder.append(escapeXml(String.valueOf(object)));
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
        String elementName = getElementName(object.getClass());
        xmlBuilder.append("<").append(elementName).append(">\n");
        indentLevel++;

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object value = field.get(object);
                addIndent();
                xmlBuilder.append("<").append(field.getName()).append(">");
                visit(value);
                xmlBuilder.append("</").append(field.getName()).append(">\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erreur lors de l'accès au champ " + field.getName(), e);
            }
        }

        indentLevel--;
        addIndent();
        xmlBuilder.append("</").append(elementName).append(">");
    }

    private void visitCollection(Collection<?> collection) {
        String elementName = "list";
        xmlBuilder.append("<").append(elementName).append(">\n");
        indentLevel++;

        for (Object item : collection) {
            addIndent();
            xmlBuilder.append("<item>");
            visit(item);
            xmlBuilder.append("</item>\n");
        }

        indentLevel--;
        addIndent();
        xmlBuilder.append("</").append(elementName).append(">");
    }

    private void visitMap(Map<?, ?> map) {
        String elementName = "map";
        xmlBuilder.append("<").append(elementName).append(">\n");
        indentLevel++;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            addIndent();
            xmlBuilder.append("<entry>\n");
            indentLevel++;
            
            addIndent();
            xmlBuilder.append("<key>").append(escapeXml(String.valueOf(entry.getKey()))).append("</key>\n");
            
            addIndent();
            xmlBuilder.append("<value>");
            visit(entry.getValue());
            xmlBuilder.append("</value>\n");
            
            indentLevel--;
            addIndent();
            xmlBuilder.append("</entry>\n");
        }

        indentLevel--;
        addIndent();
        xmlBuilder.append("</").append(elementName).append(">");
    }

    private void addIndent() {
        xmlBuilder.append("  ".repeat(indentLevel));
    }

    private String getElementName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    private String escapeXml(String str) {
        return str.replace("&", "&amp;")
                 .replace("<", "&lt;")
                 .replace(">", "&gt;")
                 .replace("\"", "&quot;")
                 .replace("'", "&apos;");
    }

    public String getXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<" + rootElementName + ">\n" +
               xmlBuilder.toString() +
               "\n</" + rootElementName + ">";
    }
} 