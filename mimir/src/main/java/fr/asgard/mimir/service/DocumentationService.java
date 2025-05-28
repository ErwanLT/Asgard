package fr.asgard.mimir.service;

import fr.asgard.mimir.annotation.ApiDescription;
import fr.asgard.mimir.config.MimirProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentationService {
    private final MimirProperties properties;

    public void generateApiDocumentation(Class<?> clazz) {
        ApiDescription description = clazz.getAnnotation(ApiDescription.class);
        if (description == null) {
            log.warn("No @ApiDescription found for class: {}", clazz.getName());
            return;
        }

        String content = generateMarkdownContent(clazz, description);
        saveDocumentation(clazz, content);
    }

    private String generateMarkdownContent(Class<?> clazz, ApiDescription description) {
        StringBuilder content = new StringBuilder();
        
        // En-tête
        content.append("# ").append(clazz.getSimpleName()).append("\n\n");
        content.append(description.value()).append("\n\n");

        // Tags
        if (description.tags().length > 0) {
            content.append("Tags: ")
                  .append(Arrays.stream(description.tags())
                        .map(tag -> "`" + tag + "`")
                        .collect(Collectors.joining(", ")))
                  .append("\n\n");
        }

        // Catégorie
        if (!description.category().isEmpty()) {
            content.append("Category: ").append(description.category()).append("\n\n");
        }

        // Méthodes
        content.append("## Methods\n\n");
        Arrays.stream(clazz.getDeclaredMethods())
              .filter(method -> method.isAnnotationPresent(ApiDescription.class))
              .forEach(method -> {
                  ApiDescription methodDesc = method.getAnnotation(ApiDescription.class);
                  content.append("### ").append(method.getName()).append("\n\n");
                  content.append(methodDesc.value()).append("\n\n");
                  
                  // Paramètres
                  if (method.getParameterCount() > 0) {
                      content.append("#### Parameters\n\n");
                      for (Parameter param : method.getParameters()) {
                          ApiDescription paramDesc = param.getAnnotation(ApiDescription.class);
                          if (paramDesc != null) {
                              String[] parts = paramDesc.value().split(":", 2);
                              String paramName = parts[0].trim();
                              String p = parts.length > 1 ? parts[1].trim() : "";
                              content.append("- `").append(paramName).append("` : ")
                                    .append(p).append("\n");
                          }
                      }
                      content.append("\n");
                  }
                  
                  // Type de retour
                  if (!methodDesc.returnType().isEmpty()) {
                      content.append("#### Returns\n\n");
                      content.append(methodDesc.returnType()).append("\n\n");
                  }
                  
                  // Exceptions
                  if (methodDesc.throws_().length > 0) {
                      content.append("#### Throws\n\n");
                      for (ApiDescription.Throws throws_ : methodDesc.throws_()) {
                          content.append("- `").append(throws_.exception().getSimpleName())
                                .append("` : ").append(throws_.description()).append("\n");
                      }
                      content.append("\n");
                  }
              });

        return content.toString();
    }

    private void saveDocumentation(Class<?> clazz, String content) {
        try {
            Path outputDir = Paths.get(properties.getDocumentation().getOutputDir());
            Files.createDirectories(outputDir);

            String fileName = clazz.getSimpleName().toLowerCase() + ".md";
            Path filePath = outputDir.resolve(fileName);
            
            Files.writeString(filePath, content);
            log.info("Documentation generated for {} at {}", clazz.getName(), filePath);
        } catch (IOException e) {
            log.error("Error generating documentation for {}: {}", clazz.getName(), e.getMessage(), e);
        }
    }
} 