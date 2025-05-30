package fr.eletutour.asgard.baldr.controller;

import fr.eletutour.asgard.baldr.model.Article;
import fr.eletutour.asgard.baldr.service.ArticleService;
import fr.eletutour.asgard.mimir.annotation.ApiDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/articles")
@ApiDescription(
        value = "API de gestion des articles",
        tags = {"articles", "api"},
        category = "api"
)
@Tag(name = "Articles", description = "API de gestion des articles")
public class ArticleController {

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "Récupérer tous les articles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @ApiDescription(
            value = "Récupère la liste de tous les articles",
            tags = {"articles", "get"},
            category = "api",
            order = 1,
            returnType = "List<Article>",
            throws_ = {
                @ApiDescription.Throws(
                    exception = Exception.class,
                    description = "Erreur lors de la récupération des articles"
                )
            }
    )
    @GetMapping
    public List<Article> getArticles() {
        return articleService.getArticles();
    }

    @Operation(summary = "Récupérer un article par ID avec timeout")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Article trouvé",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "404", description = "Article non trouvé"),
        @ApiResponse(responseCode = "408", description = "Délai d'attente dépassé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @ApiDescription(
            value = "Récupère un article par son ID avec un délai d'attente",
            tags = {"articles", "get"},
            category = "api",
            order = 2,
            returnType = "ResponseEntity<Article>",
            throws_ = {
                @ApiDescription.Throws(
                    exception = TimeoutException.class,
                    description = "Le délai d'attente a été dépassé"
                ),
                @ApiDescription.Throws(
                    exception = InterruptedException.class,
                    description = "L'opération a été interrompue"
                ),
                @ApiDescription.Throws(
                    exception = ExecutionException.class,
                    description = "Une erreur s'est produite lors de l'exécution"
                )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleByIdAvecTimeout(@Parameter(name = "id", description = "ID de l'article à récupérer") 
                                                           @PathVariable("id") 
                                                           @ApiDescription("id : Id de l'article à récupérer") Long id) throws TimeoutException, InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Article> future = executor.submit(() -> articleService.getArticleById(id));

        try {
            Article article = future.get(2, TimeUnit.SECONDS); // Timeout de 2 secondes
            return ResponseEntity.ok(article);
        } finally {
            executor.shutdownNow();
        }
    }
}
