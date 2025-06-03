package fr.eletutour.asgard.mimir.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UmlDiagramService {

    public String generateClassDiagram(Class<?> clazz) {
        return generateMermaidSource(clazz);
    }

    private String generateMermaidSource(Class<?> clazz) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("classDiagram\n");

        // Ajouter la classe principale
        mermaid.append("    class ").append(clazz.getSimpleName()).append(" {\n");

        // Ajouter les champs
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            mermaid.append("        ").append(getFieldModifier(field))
                    .append(field.getType().getSimpleName())
                    .append(" ")
                    .append(field.getName())
                    .append("\n");
        }

        // Ajouter les méthodes
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic()) {
                mermaid.append("        ").append(getMethodModifier(method))
                        .append(method.getReturnType().getSimpleName())
                        .append(" ")
                        .append(method.getName())
                        .append("(")
                        .append(getMethodParameters(method))
                        .append(")\n");
            }
        }

        mermaid.append("    }\n\n");

        // Ajouter les relations
        addRelations(mermaid, clazz);

        return mermaid.toString();
    }

    private String getFieldModifier(java.lang.reflect.Field field) {
        if (java.lang.reflect.Modifier.isPrivate(field.getModifiers())) return "-";
        if (java.lang.reflect.Modifier.isProtected(field.getModifiers())) return "#";
        if (java.lang.reflect.Modifier.isPublic(field.getModifiers())) return "+";
        return "~";
    }

    private String getMethodModifier(java.lang.reflect.Method method) {
        if (java.lang.reflect.Modifier.isPrivate(method.getModifiers())) return "-";
        if (java.lang.reflect.Modifier.isProtected(method.getModifiers())) return "#";
        if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) return "+";
        return "~";
    }

    private String getMethodParameters(java.lang.reflect.Method method) {
        List<String> params = new ArrayList<>();
        for (java.lang.reflect.Parameter param : method.getParameters()) {
            params.add(param.getType().getSimpleName() + " " + param.getName());
        }
        return String.join(", ", params);
    }

    private void addRelations(StringBuilder mermaid, Class<?> clazz) {
        // Héritage
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            mermaid.append("    ").append(clazz.getSimpleName())
                    .append(" --|> ")
                    .append(superclass.getSimpleName())
                    .append("\n");
        }

        // Interfaces
        for (Class<?> iface : clazz.getInterfaces()) {
            mermaid.append("    ").append(clazz.getSimpleName())
                    .append(" ..|> ")
                    .append(iface.getSimpleName())
                    .append("\n");
        }

        // Associations (basées sur les types des champs)
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (!fieldType.isPrimitive() && !fieldType.getName().startsWith("java.lang")) {
                mermaid.append("    ").append(clazz.getSimpleName())
                        .append(" --> ")
                        .append(fieldType.getSimpleName())
                        .append(" : ")
                        .append(field.getName())
                        .append("\n");
            }
        }
    }
}