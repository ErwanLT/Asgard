package fr.eletutour.asgard.loki.controller;

import fr.eletutour.asgard.loki.model.ChaosStatus;
import fr.eletutour.asgard.loki.service.ChaosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/loki")
@Tag(name = "Loki Controller", description = "Controller for Loki operations")
@ConditionalOnProperty(name = "loki.enabled", havingValue = "true", matchIfMissing = false)
public class LokiController {

    private final ChaosService chaosService;

    public LokiController(ChaosService chaosService) {
        this.chaosService = chaosService;
    }

    @Operation(
        summary = "Activation du mode chaos",
        description = "Active le mode chaos dans Loki. Ce mode permet de simuler des perturbations dans le système pour tester la résilience et la robustesse des services."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mode chaos activé avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ChaosStatus.class))
    )})
    @PostMapping("/enable")
    public ResponseEntity<ChaosStatus> cloneArticle() {
        return ResponseEntity.ok(chaosService.enableChaos());
    }

    @Operation(
        summary = "Désactivation du mode chaos",
        description = "Désactive le mode chaos dans Loki. Cela permet de revenir à un état normal de fonctionnement après des tests de résilience."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mode chaos désactivé avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ChaosStatus.class))
    )})
    @PostMapping("/disable")
    public ResponseEntity<ChaosStatus> disableChaos() {
        return ResponseEntity.ok(chaosService.disableChaos());
    }
}
