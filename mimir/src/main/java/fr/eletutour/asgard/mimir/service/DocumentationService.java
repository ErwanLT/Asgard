package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.config.MimirProperties;
import fr.eletutour.asgard.mimir.model.Documentation;
import fr.eletutour.asgard.mimir.util.ClassFinder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentationService {
    private final MimirProperties properties;
    private final UmlDiagramService umlDiagramService;
    private final SearchService searchService;

    public Documentation generateDocumentation(Class<?> clazz) {
        log.info("Génération de la documentation pour la classe: {}", clazz.getName());
        try {
            Tag tag = clazz.getAnnotation(Tag.class);
            if (tag == null) {
                log.warn("No @Tag found for class: {}", clazz.getName());
                return null;
            }

            String markdownContent = generateMarkdownContent(clazz);
            Path outputPath = properties.getOutputPath().resolve(clazz.getSimpleName().toLowerCase() + ".md");
            Files.writeString(outputPath, markdownContent);
            log.info("Documentation générée avec succès dans: {}", outputPath);
            
            Documentation documentation = createDocumentation(clazz, markdownContent);
            searchService.saveDocumentation(documentation);

            // Génération du diagramme UML
            umlDiagramService.generateClassDiagram(clazz);
            
            return documentation;
        } catch (IOException e) {
            log.error("Erreur lors de la génération de la documentation", e);
            throw new RuntimeException("Erreur lors de la génération de la documentation", e);
        }
    }

    public List<Documentation> generatePackageDocumentation(String packageName) throws ClassNotFoundException {
        log.info("Génération de la documentation pour le package: {}", packageName);
        List<Documentation> documentations = new ArrayList<>();
        
        // Récupérer toutes les classes du package
        List<Class<?>> classes = ClassFinder.findClassesInPackage(packageName);
        log.info("{} classes trouvées dans le package {}", classes.size(), packageName);
        
        // Générer la documentation pour chaque classe
        for (Class<?> clazz : classes) {
            Documentation documentation = generateDocumentation(clazz);
            if (documentation != null) {
                documentations.add(documentation);
            }
        }
        
        return documentations;
    }

    private Documentation createDocumentation(Class<?> clazz, String markdownContent) {
        Documentation documentation = new Documentation();
        documentation.setTitle(clazz.getSimpleName());
        documentation.setContent(markdownContent);
        return documentation;
    }

    private String generateMarkdownContent(Class<?> clazz) {
        Tag tag = clazz.getAnnotation(Tag.class);
        StringBuilder content = new StringBuilder();
        
        // En-tête
        content.append("# ").append(clazz.getSimpleName()).append("\n\n");
        content.append(tag.description()).append("\n\n");

        // Diagramme de classe avec Mermaid
        content.append("## Diagramme de Classe\n\n");
        content.append("```mermaid\n");
        content.append("classDiagram\n");
        
        // Ajouter la classe principale
        content.append("    class ").append(clazz.getSimpleName()).append(" {\n");
        
        // Ajouter les champs
        Arrays.stream(clazz.getDeclaredFields())
              .forEach(field -> {
                  String visibility = Modifier.isPrivate(field.getModifiers()) ? "-" : "+";
                  content.append("        ").append(visibility)
                        .append(field.getType().getSimpleName())
                        .append(" ")
                        .append(field.getName())
                        .append("\n");
              });
        
        // Ajouter les méthodes publiques
        Arrays.stream(clazz.getDeclaredMethods())
              .filter(method -> Modifier.isPublic(method.getModifiers()))
              .forEach(method -> {
                  String methodName = method.getName();
                  String returnType = method.getReturnType().getSimpleName();
                  String parameters = Arrays.stream(method.getParameters())
                      .map(param -> param.getType().getSimpleName() + " " + param.getName())
                      .collect(Collectors.joining(", "));
                  
                  content.append("        +").append(returnType).append(" ").append(methodName)
                        .append("(").append(parameters).append(")\n");
              });
        
        content.append("    }\n");
        
        // Ajouter les relations avec les autres classes
        Arrays.stream(clazz.getDeclaredFields())
              .filter(field -> !field.getType().isPrimitive() && !field.getType().getName().startsWith("java.lang"))
              .forEach(field -> {
                  content.append("    ").append(clazz.getSimpleName())
                        .append(" --> ").append(field.getType().getSimpleName())
                        .append(" : ").append(field.getName()).append("\n");
              });
        
        content.append("```\n\n");

        // Méthodes
        content.append("## Methods\n\n");
        Arrays.stream(clazz.getDeclaredMethods())
              .filter(method -> method.isAnnotationPresent(Operation.class))
              .forEach(method -> {
                  Operation operation = method.getAnnotation(Operation.class);
                  content.append("### ").append(method.getName()).append("\n\n");
                  content.append(operation.description()).append("\n\n");
                  
                  // Paramètres
                  if (method.getParameterCount() > 0) {
                      content.append("#### Parameters\n\n");
                      for (java.lang.reflect.Parameter param : method.getParameters()) {
                          Parameter parameter = param.getAnnotation(Parameter.class);
                          if (parameter != null) {
                              content.append("- `").append(param.getName()).append("` : ")
                                    .append(parameter.description()).append("\n");
                          }
                      }
                      content.append("\n");
                  }
                  
                  // Réponses
                  ApiResponse[] responses = method.getAnnotationsByType(ApiResponse.class);
                  if (responses.length > 0) {
                      content.append("#### Responses\n\n");
                      for (ApiResponse response : responses) {
                          content.append("- `").append(response.responseCode()).append("` : ")
                                .append(response.description()).append("\n");
                      }
                      content.append("\n");
                  }
              });

        return content.toString();
    }

    public Documentation getDocumentation(String className) {
        return searchService.findDocumentationByTitle(className);
    }
}