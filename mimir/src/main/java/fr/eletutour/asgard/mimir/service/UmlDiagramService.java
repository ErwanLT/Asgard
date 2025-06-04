package fr.eletutour.asgard.mimir.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;

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
        for (Field field : clazz.getDeclaredFields()) {
            mermaid.append("        ").append(getFieldModifier(field))
                    .append(field.getType().getSimpleName())
                    .append(" ")
                    .append(field.getName())
                    .append("\n");
        }

        // Ajouter les méthodes
        for (Method method : clazz.getDeclaredMethods()) {
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
        if (isPrivate(field.getModifiers())) return "-";
        if (isProtected(field.getModifiers())) return "#";
        if (isPublic(field.getModifiers())) return "+";
        return "~";
    }

    private String getMethodModifier(java.lang.reflect.Method method) {
        if (isPrivate(method.getModifiers())) return "-";
        if (isProtected(method.getModifiers())) return "#";
        if (isPublic(method.getModifiers())) return "+";
        return "~";
    }

    private String getMethodParameters(java.lang.reflect.Method method) {
        List<String> params = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
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
        for (Field field : clazz.getDeclaredFields()) {
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