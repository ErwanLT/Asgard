package fr.eletutour.asgard.mimir.controller;

import fr.eletutour.asgard.mimir.annotation.ApiDescription;
import fr.eletutour.asgard.mimir.service.DocumentationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mimir")
@ApiDescription(
        value = "API de documentation Mimir",
        tags = {"documentation", "api"},
        category = "api"
)
@Tag(name = "Mimir", description = "API de gestion de la documentation de l'application")
public class MimirController {

    @Autowired
    private DocumentationService documentationService;

    @PostMapping("/generate/class")
    @ApiDescription("Génère la documentation pour une classe spécifique")
    public ResponseEntity<Void> generateClassDocumentation(@RequestParam String className) {
        try {
            Class<?> clazz = Class.forName(className);
            documentationService.generateDocumentation(clazz);
            return ResponseEntity.ok().build();
        } catch (ClassNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate/package")
    @ApiDescription("Génère la documentation pour toutes les classes d'un package")
    public ResponseEntity<Void> generatePackageDocumentation(@RequestParam String packageName) {
        try {
            Class<?> clazz = Class.forName(packageName);
            documentationService.generateDocumentation(clazz);
            return ResponseEntity.ok().build();
        } catch (ClassNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}