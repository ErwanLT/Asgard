package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.config.MimirProperties;
import fr.eletutour.asgard.mimir.model.Documentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentationService {
    private final MimirProperties properties;
    private final UmlDiagramService umlDiagramService;
    private final SearchService searchService;

    public void generateDocumentation(Class<?> clazz) {
        log.info("Génération de la documentation pour la classe: {}", clazz.getName());
        try {
            Tag tag = clazz.getAnnotation(Tag.class);
            if (tag == null) {
                log.warn("No @Tag found for class: {}", clazz.getName());
                return;
            }

            String markdownContent = generateMarkdownContent(clazz);
            Path outputPath = properties.getOutputPath().resolve(clazz.getSimpleName().toLowerCase() + ".md");
            Files.writeString(outputPath, markdownContent);
            log.info("Documentation générée avec succès dans: {}", outputPath);
            searchService.saveDocumentation(createDocumentation(clazz, markdownContent));

            // Génération du diagramme UML
            umlDiagramService.generateClassDiagram(clazz);
        } catch (IOException e) {
            log.error("Erreur lors de la génération de la documentation", e);
            throw new RuntimeException("Erreur lors de la génération de la documentation", e);
        }
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

        // Diagramme UML
        content.append("## Diagramme de Classe\n\n");
        content.append("![Diagramme UML](")
              .append("diagrams/")
              .append(clazz.getSimpleName().toLowerCase())
              .append("_diagram.png")
              .append(")\n\n");

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