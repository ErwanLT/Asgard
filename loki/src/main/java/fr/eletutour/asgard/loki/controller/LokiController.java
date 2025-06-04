package fr.eletutour.asgard.loki.controller;

import fr.eletutour.asgard.loki.model.ChaosStatus;
import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import fr.eletutour.asgard.loki.service.ChaosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                        schema = @Schema(implementation = ChaosStatus.class)))
    })
    @PostMapping("/enable")
    public ResponseEntity<ChaosStatus> enableChaos() {
        return ResponseEntity.ok(chaosService.enableChaos());
    }

    @Operation(
        summary = "Désactivation du mode chaos",
        description = "Désactive le mode chaos dans Loki. Cela permet de revenir à un état normal de fonctionnement après des tests de résilience."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mode chaos désactivé avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ChaosStatus.class)))
    })
    @PostMapping("/disable")
    public ResponseEntity<ChaosStatus> disableChaos() {
        return ResponseEntity.ok(chaosService.disableChaos());
    }

    @Operation(
        summary = "Mise à jour des watchers",
        description = "Configure les watchers (Hugin) pour surveiller les différentes couches de l'application."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Watchers mis à jour avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Hugin.class)))
    })
    @PutMapping("/watcher")
    public ResponseEntity<Hugin> updateWatcher(@RequestBody Hugin watcher) {
        return ResponseEntity.ok(chaosService.updateWatcher(watcher));
    }

    @Operation(
        summary = "Mise à jour des perturbations",
        description = "Configure les types de perturbations (Munin) à appliquer dans le système."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perturbations mises à jour avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Munin.class))),
        @ApiResponse(responseCode = "400", description = "Configuration invalide")
    })
    @PutMapping("/chaos")
    public ResponseEntity<Munin> updateChaosType(@RequestBody Munin chaosType) {
        return ResponseEntity.ok(chaosService.updateChaosType(chaosType));
    }

    @Operation(
        summary = "État actuel",
        description = "Récupère l'état actuel de la configuration du chaos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "État récupéré avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = LokiChaos.class)))
    })
    @GetMapping("/state")
    public ResponseEntity<LokiChaos> getCurrentState() {
        return ResponseEntity.ok(chaosService.getCurrentState());
    }
}
