package fr.eletutour.asgard.loki.controller;

import fr.eletutour.asgard.loki.model.ChaosStatus;
import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import fr.eletutour.asgard.loki.service.ChaosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(
    name = "Loki",
    description = """
        API de gestion du Chaos Engineering. 
        Permet de simuler des perturbations contrôlées dans le système pour tester sa résilience.
        """,
    externalDocs = @io.swagger.v3.oas.annotations.ExternalDocumentation(
        description = "Documentation sur le Chaos Engineering",
        url = "https://principlesofchaos.org/"
    )
)
@ConditionalOnProperty(name = "loki.enabled", havingValue = "true", matchIfMissing = false)
public class LokiController {

    private final ChaosService chaosService;

    public LokiController(ChaosService chaosService) {
        this.chaosService = chaosService;
    }

    @Operation(
        summary = "Activation du mode chaos",
        description = """
            Active le mode chaos dans Loki. 
            Ce mode permet de simuler des perturbations dans le système pour tester la résilience et la robustesse des services.
            Une fois activé, le chaos sera appliqué selon la configuration des watchers (Hugin) et des types de perturbations (Munin).
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mode chaos activé avec succès. Le système commencera à appliquer les perturbations configurées.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChaosStatus.class)
            )
        )
    })
    @PostMapping("/enable")
    public ResponseEntity<ChaosStatus> enableChaos() {
        return ResponseEntity.ok(chaosService.enableChaos());
    }

    @Operation(
        summary = "Désactivation du mode chaos",
        description = """
            Désactive le mode chaos dans Loki. 
            Cela permet de revenir à un état normal de fonctionnement après des tests de résilience.
            Toutes les perturbations en cours seront arrêtées immédiatement.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mode chaos désactivé avec succès. Le système revient à un fonctionnement normal.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChaosStatus.class)
            )
        )
    })
    @PostMapping("/disable")
    public ResponseEntity<ChaosStatus> disableChaos() {
        return ResponseEntity.ok(chaosService.disableChaos());
    }

    @Operation(
        summary = "Configuration des watchers",
        description = """
            Configure les watchers (Hugin) pour surveiller les différentes couches de l'application.
            Les watchers déterminent quelles couches de l'application seront affectées par le chaos :
            - RestController : Les endpoints REST
            - Service : La couche service
            - Repository : La couche d'accès aux données
            - Controller : Les contrôleurs Spring MVC
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration des watchers mise à jour avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Hugin.class)
            )
        )
    })
    @PutMapping("/watcher")
    public ResponseEntity<Hugin> updateWatcher(
        @Parameter(
            description = "Configuration des watchers",
            required = true,
            schema = @Schema(implementation = Hugin.class)
        )
        @RequestBody Hugin watcher
    ) {
        return ResponseEntity.ok(chaosService.updateWatcher(watcher));
    }

    @Operation(
        summary = "Configuration des perturbations",
        description = """
            Configure les types de perturbations (Munin) à appliquer dans le système.
            Permet de définir :
            - Le niveau de chaos (0-100) : probabilité d'application des perturbations
            - La latence : délai aléatoire à ajouter aux réponses
            - Les exceptions : simulation d'erreurs système
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration des perturbations mise à jour avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Munin.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Configuration invalide. Vérifiez les valeurs des paramètres.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = String.class)
            )
        )
    })
    @PutMapping("/chaos")
    public ResponseEntity<Munin> updateChaosType(
        @Parameter(
            description = "Configuration des perturbations",
            required = true,
            schema = @Schema(implementation = Munin.class)
        )
        @RequestBody Munin chaosType
    ) {
        return ResponseEntity.ok(chaosService.updateChaosType(chaosType));
    }

    @Operation(
        summary = "État du chaos",
        description = """
            Récupère l'état actuel de la configuration du chaos, incluant :
            - L'état d'activation du chaos
            - La configuration des watchers
            - Les types de perturbations configurés
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "État récupéré avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LokiChaos.class)
            )
        )
    })
    @GetMapping("/state")
    public ResponseEntity<LokiChaos> getCurrentState() {
        return ResponseEntity.ok(chaosService.getCurrentState());
    }
}
