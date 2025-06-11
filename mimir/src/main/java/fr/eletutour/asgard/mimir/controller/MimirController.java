package fr.eletutour.asgard.mimir.controller;

import fr.eletutour.asgard.mimir.model.Documentation;
import fr.eletutour.asgard.mimir.service.DocumentationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mimir")
@Tag(name = "Mimir", description = "API de gestion de la documentation de l'application")
@ConditionalOnProperty(name = "mimir.enabled", havingValue = "true", matchIfMissing = false)
public class MimirController {

    @Autowired
    private DocumentationService documentationService;

    @PostMapping("/generate/class")
    @Operation(
        summary = "Génère la documentation pour une classe spécifique",
        description = "Génère la documentation au format Markdown pour une classe Java spécifiée par son nom complet"
    )
    @ApiResponse(responseCode = "200", description = "Documentation générée avec succès")
    @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    public ResponseEntity<Documentation> generateClassDocumentation(
        @Parameter(description = "Nom complet de la classe à documenter") @RequestParam String className
    ) {
        try {
            Class<?> clazz = Class.forName(className);
            Documentation documentation = documentationService.generateDocumentation(clazz);
            return ResponseEntity.ok(documentation);
        } catch (ClassNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate/package")
    @Operation(
        summary = "Génère la documentation pour toutes les classes d'un package",
        description = "Génère la documentation au format Markdown pour toutes les classes Java dans un package spécifié"
    )
    @ApiResponse(responseCode = "200", description = "Documentations générées avec succès")
    @ApiResponse(responseCode = "404", description = "Package non trouvé")
    public ResponseEntity<List<Documentation>> generatePackageDocumentation(
        @Parameter(description = "Nom du package à documenter") @RequestParam String packageName) {
        try {
            List<Documentation> documentations = documentationService.generatePackageDocumentation(packageName);
            return ResponseEntity.ok(documentations);
        } catch (ClassNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}