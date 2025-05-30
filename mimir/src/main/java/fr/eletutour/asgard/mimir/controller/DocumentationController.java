package fr.eletutour.asgard.mimir.controller;

import fr.eletutour.asgard.mimir.annotation.ApiDescription;
import fr.eletutour.asgard.mimir.service.DocumentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documentation")
@ApiDescription(
        value = "API de documentation Mimir",
        tags = {"documentation", "api"},
        category = "api"
)
public class DocumentationController {

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