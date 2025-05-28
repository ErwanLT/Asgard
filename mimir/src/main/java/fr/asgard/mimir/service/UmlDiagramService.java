package fr.asgard.mimir.service;

import fr.asgard.mimir.config.MimirProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UmlDiagramService {
    private final MimirProperties properties;

    public void generateClassDiagram(Class<?> clazz) {
        try {
            String plantUmlSource = generatePlantUmlSource(clazz);
            savePlantUmlSource(plantUmlSource, clazz.getSimpleName());
            generateDiagram(plantUmlSource, clazz.getSimpleName());
        } catch (IOException e) {
            log.error("Erreur lors de la génération du diagramme pour {}: {}", clazz.getName(), e.getMessage(), e);
        }
    }

    private String generatePlantUmlSource(Class<?> clazz) {
        StringBuilder plantUml = new StringBuilder();
        plantUml.append("@startuml\n");
        plantUml.append("skinparam classAttributeIconSize 0\n");
        plantUml.append("skinparam monochrome true\n");
        plantUml.append("skinparam shadowing false\n");
        plantUml.append("skinparam defaultFontName Arial\n");
        plantUml.append("skinparam defaultFontSize 12\n\n");

        // Ajouter la classe principale
        plantUml.append("class ").append(clazz.getSimpleName()).append(" {\n");
        
        // Ajouter les champs
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            plantUml.append("  ").append(getFieldModifier(field))
                   .append(field.getType().getSimpleName())
                   .append(" ")
                   .append(field.getName())
                   .append("\n");
        }
        
        // Ajouter les méthodes
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic()) {
                plantUml.append("  ").append(getMethodModifier(method))
                       .append(method.getReturnType().getSimpleName())
                       .append(" ")
                       .append(method.getName())
                       .append("(")
                       .append(getMethodParameters(method))
                       .append(")\n");
            }
        }
        
        plantUml.append("}\n\n");

        // Ajouter les relations
        addRelations(plantUml, clazz);

        plantUml.append("@enduml");
        return plantUml.toString();
    }

    private String getFieldModifier(java.lang.reflect.Field field) {
        if (java.lang.reflect.Modifier.isPrivate(field.getModifiers())) return "- ";
        if (java.lang.reflect.Modifier.isProtected(field.getModifiers())) return "# ";
        if (java.lang.reflect.Modifier.isPublic(field.getModifiers())) return "+ ";
        return "~ ";
    }

    private String getMethodModifier(java.lang.reflect.Method method) {
        if (java.lang.reflect.Modifier.isPrivate(method.getModifiers())) return "- ";
        if (java.lang.reflect.Modifier.isProtected(method.getModifiers())) return "# ";
        if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) return "+ ";
        return "~ ";
    }

    private String getMethodParameters(java.lang.reflect.Method method) {
        List<String> params = new ArrayList<>();
        for (java.lang.reflect.Parameter param : method.getParameters()) {
            params.add(param.getType().getSimpleName() + " " + param.getName());
        }
        return String.join(", ", params);
    }

    private void addRelations(StringBuilder plantUml, Class<?> clazz) {
        // Héritage
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            plantUml.append(clazz.getSimpleName())
                   .append(" --|> ")
                   .append(superclass.getSimpleName())
                   .append("\n");
        }

        // Interfaces
        for (Class<?> iface : clazz.getInterfaces()) {
            plantUml.append(clazz.getSimpleName())
                   .append(" ..|> ")
                   .append(iface.getSimpleName())
                   .append("\n");
        }

        // Associations (basées sur les types des champs)
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (!fieldType.isPrimitive() && !fieldType.getName().startsWith("java.lang")) {
                plantUml.append(clazz.getSimpleName())
                       .append(" --> ")
                       .append(fieldType.getSimpleName())
                       .append("\n");
            }
        }
    }

    private void generateDiagram(String plantUmlSource, String className) throws IOException {
        Path outputDir = Paths.get(properties.getDocumentation().getOutputDir(), "diagrams");
        Files.createDirectories(outputDir);

        String fileName = className.toLowerCase() + "_diagram.png";
        Path filePath = outputDir.resolve(fileName);

        try (FileOutputStream output = new FileOutputStream(filePath.toFile())) {
            SourceStringReader reader = new SourceStringReader(plantUmlSource);
            reader.outputImage(output, new FileFormatOption(FileFormat.PNG));
        }

        log.info("Diagramme UML généré pour {} à {}", className, filePath);
    }

    private void savePlantUmlSource(String plantUmlSource, String className) throws IOException {
        Path outputDir = Paths.get(properties.getDocumentation().getOutputDir(), "diagrams");
        Files.createDirectories(outputDir);

        String fileName = className.toLowerCase() + "_diagram.puml";
        Path filePath = outputDir.resolve(fileName);

        Files.writeString(filePath, plantUmlSource);
        log.info("Source PlantUML sauvegardée pour {} à {}", className, filePath);
    }
} 