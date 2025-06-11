package fr.eletutour.asgard.hel.controller;

import fr.eletutour.asgard.hel.service.HelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hel")
@Tag(
    name = "Hel",
    description = """
        API de gestion de l'arrêt de l'application.
        
        Cette API permet de contrôler l'arrêt de l'application de deux manières :
        - Arrêt immédiat : arrête l'application après un délai de 1 seconde
        - Arrêt programmé : planifie l'arrêt selon une expression cron
        
        ⚠️ Attention : Ces opérations sont irréversibles et doivent être utilisées avec précaution.
        """
)
@SecurityRequirement(name = "bearerAuth")
@ConditionalOnProperty(name = "hel.enabled", havingValue = "true", matchIfMissing = false)
public class HelController {

    private final HelService shutdownService;

    public HelController(HelService shutdownService) {
        this.shutdownService = shutdownService;
    }

    @Operation(
        summary = "Arrêt immédiat",
        description = """
            Arrête l'application immédiatement après un délai de 1 seconde.
            
            Cette opération est irréversible et doit être utilisée avec précaution.
            L'application s'arrêtera proprement en :
            1. Arrêtant l'acceptation de nouvelles requêtes
            2. Attendant la fin des requêtes en cours
            3. Fermant les connexions aux bases de données
            4. Arrêtant le serveur web
            
            ⚠️ Attention : Cette opération ne peut pas être annulée une fois initiée.
            """,
        operationId = "shutdownImmediate"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Arrêt initié avec succès",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    value = "Application shutdown initiated",
                    description = "Message de confirmation de l'initiation de l'arrêt"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = """
                Accès non autorisé - Droits insuffisants pour effectuer l'arrêt.
                
                Vérifiez que vous avez les droits d'administration nécessaires.
                """
        ),
        @ApiResponse(
            responseCode = "503",
            description = """
                Service indisponible - L'application est déjà en cours d'arrêt.
                
                Cette erreur survient si une opération d'arrêt est déjà en cours.
                """
        )
    })
    @PostMapping("/immediate")
    public ResponseEntity<String> shutdownImmediate() {
        shutdownService.shutdownImmediate();
        return ResponseEntity.ok("Application shutdown initiated");
    }

    @Operation(
        summary = "Arrêt programmé",
        description = """
            Programme l'arrêt de l'application selon une expression cron.
            
            L'arrêt sera effectué à la prochaine occurrence correspondant à l'expression cron fournie.
            Le processus d'arrêt suit les mêmes étapes que l'arrêt immédiat :
            1. Arrêt de l'acceptation de nouvelles requêtes
            2. Attente de la fin des requêtes en cours
            3. Fermeture des connexions aux bases de données
            4. Arrêt du serveur web
            
            ⚠️ Attention : Une fois programmé, l'arrêt ne peut être annulé que par un redémarrage de l'application.
            """,
        operationId = "shutdownScheduled"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Arrêt programmé avec succès",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    value = "Scheduled shutdown configured with cron: 0 0 0 * * ?",
                    description = "Message de confirmation avec l'expression cron utilisée"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = """
                Expression cron invalide - Le format de l'expression cron n'est pas valide.
                
                L'expression doit suivre le format standard cron avec 6 champs :
                - secondes (0-59)
                - minutes (0-59)
                - heures (0-23)
                - jour du mois (1-31)
                - mois (1-12 ou JAN-DEC)
                - jour de la semaine (0-6 ou SUN-SAT)
                """
        ),
        @ApiResponse(
            responseCode = "403",
            description = """
                Accès non autorisé - Droits insuffisants pour programmer l'arrêt.
                
                Vérifiez que vous avez les droits d'administration nécessaires.
                """
        ),
        @ApiResponse(
            responseCode = "409",
            description = """
                Conflit - Un arrêt est déjà programmé.
                
                Une seule planification d'arrêt peut être active à la fois.
                """
        )
    })
    @PostMapping("/scheduled")
    public ResponseEntity<String> shutdownScheduled(
        @Parameter(
            description = """
                Expression cron pour programmer l'arrêt.
                
                Format standard cron avec 6 champs :
                - secondes (0-59)
                - minutes (0-59)
                - heures (0-23)
                - jour du mois (1-31)
                - mois (1-12 ou JAN-DEC)
                - jour de la semaine (0-6 ou SUN-SAT)
                
                Exemples :
                - "0 0 0 * * ?" : tous les jours à minuit
                - "0 0 12 * * ?" : tous les jours à midi
                - "0 0 0 ? * MON" : tous les lundis à minuit
                """,
            example = "0 0 0 * * ?",
            required = true,
            schema = @Schema(
                type = "string",
                pattern = "^\\s*($|#|\\w+\\s*=|(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[01]?\\d|2[0-3])(?:(?:-|/|\\,)(?:[01]?\\d|2[0-3]))?(?:,(?:[01]?\\d|2[0-3])(?:(?:-|/|\\,)(?:[01]?\\d|2[0-3]))?)*)\\s+(\\?|\\*|(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?(?:,(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?)*)\\s+(\\?|\\*|(?:[1-9]|1[012])(?:(?:-|/|\\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|/|\\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\\?|\\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\\s+(\\?|\\*|(?:[0-6])(?:(?:-|/|\\,)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|/|\\,)(?:[0-6]))?(?:L)?)*|\\?|\\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\\s)+(\\?|\\*|(?:|\\d{4})(?:(?:-|/|\\,)(?:|\\d{4}))?(?:,(?:|\\d{4})(?:(?:-|/|\\,)(?:|\\d{4}))?)*))$"
            )
        )
        @RequestParam(name = "cronExpression") String cronExpression
    ) {
        shutdownService.scheduleShutdown(cronExpression);
        return ResponseEntity.ok("Scheduled shutdown configured with cron: " + cronExpression);
    }
}
