package fr.eletutour.asgard.hel.controller;

import fr.eletutour.asgard.hel.service.HelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hel")
@Tag(name = "Hel", description = "API de gestion de l'arrêt de l'application")
public class HelController {

    private final HelService shutdownService;

    public HelController(HelService shutdownService) {
        this.shutdownService = shutdownService;
    }

    @Operation(
        summary = "Arrêt immédiat",
        description = "Arrête l'application immédiatement après un délai de 1 seconde"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Arrêt initié avec succès",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(implementation = String.class)
            )
        )
    })
    @PostMapping("/immediate")
    public ResponseEntity<String> shutdownImmediate() {
        shutdownService.shutdownImmediate();
        return ResponseEntity.ok("Application shutdown initiated");
    }

    @Operation(
        summary = "Arrêt programmé",
        description = "Programme l'arrêt de l'application selon une expression cron"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Arrêt programmé avec succès",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(implementation = String.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Expression cron invalide"
        )
    })
    @PostMapping("/scheduled")
    public ResponseEntity<String> shutdownScheduled(
        @Parameter(
            description = "Expression cron pour programmer l'arrêt",
            example = "0 0 0 * * ?",
            required = true
        )
        @RequestParam(name = "cronExpression") String cronExpression
    ) {
        shutdownService.scheduleShutdown(cronExpression);
        return ResponseEntity.ok("Scheduled shutdown configured with cron: " + cronExpression);
    }
}
