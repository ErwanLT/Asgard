package fr.eletutour.asgard.mimir.controller;

import fr.eletutour.asgard.mimir.model.Documentation;
import fr.eletutour.asgard.mimir.service.DocumentationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mimir")
@Tag(name = "Mimir", description = "API de gestion de la documentation de l'application")
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
    public ResponseEntity<Void> generateClassDocumentation(
        @Parameter(description = "Nom complet de la classe à documenter") @RequestParam String className
    ) {
        try {
            Class<?> clazz = Class.forName(className);
            documentationService.generateDocumentation(clazz);
            return ResponseEntity.ok().build();
        } catch (ClassNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate/package")
    @Operation(
        summary = "Génère la documentation pour toutes les classes d'un package",
        description = "Génère la documentation au format Markdown pour toutes les classes Java dans un package spécifié"
    )
    @ApiResponse(responseCode = "200", description = "Documentation générée avec succès")
    @ApiResponse(responseCode = "404", description = "Package non trouvé")
    public ResponseEntity<Void> generatePackageDocumentation(
        @Parameter(description = "Nom du package à documenter") @RequestParam String packageName
    ) {
        try {
            Class<?> clazz = Class.forName(packageName);
            documentationService.generateDocumentation(clazz);
            return ResponseEntity.ok().build();
        } catch (ClassNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/documentation/{className}")
    @Operation(
        summary = "Récupère la documentation générée",
        description = "Récupère la documentation au format Markdown pour une classe spécifiée"
    )
    @ApiResponse(responseCode = "200", description = "Documentation trouvée")
    @ApiResponse(responseCode = "404", description = "Documentation non trouvée")
    public ResponseEntity<Documentation> getDocumentation(
        @Parameter(description = "Nom de la classe dont on veut récupérer la documentation") 
        @PathVariable("className") String className
    ) {
        Documentation documentation = documentationService.getDocumentation(className);
        if (documentation != null) {
            return ResponseEntity.ok(documentation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}