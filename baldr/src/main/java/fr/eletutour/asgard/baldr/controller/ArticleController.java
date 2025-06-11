package fr.eletutour.asgard.baldr.controller;

import fr.eletutour.asgard.baldr.exception.ArticleNotFoundException;
import fr.eletutour.asgard.baldr.exception.AuthorNotFoundException;
import fr.eletutour.asgard.baldr.model.Article;
import fr.eletutour.asgard.baldr.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Articles", description = """
    API de gestion des articles permettant de :
    - Lister tous les articles
    - Récupérer un article par son ID (avec timeout de 2 secondes)
    - Créer un nouvel article associé à un auteur
    - Mettre à jour le contenu d'un article
    - Supprimer un article
    - Cloner un article existant
    
    Chaque article possède un titre, un contenu et est associé à un auteur.
    Les opérations de lecture sont optimisées avec des timeouts pour garantir la réactivité.
    """)
public class ArticleController {

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(
        summary = "Récupérer tous les articles",
        description = "Récupère la liste complète des articles disponibles dans le système. Les articles sont retournés avec leurs informations détaillées et les références à leurs auteurs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public List<Article> getArticles() {
        return articleService.getAllArticles();
    }

    @Operation(
        summary = "Récupérer un article par ID avec timeout",
        description = "Récupère les informations détaillées d'un article spécifique en utilisant son identifiant unique. L'opération est limitée à 2 secondes pour garantir la réactivité du système."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Article trouvé",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "404", description = "Article non trouvé"),
        @ApiResponse(responseCode = "408", description = "Délai d'attente dépassé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleByIdAvecTimeout(@Parameter(name = "id", description = "ID de l'article à récupérer") 
                                                           @PathVariable("id")  Long id) throws TimeoutException, InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Article> future = executor.submit(() -> articleService.getArticleById(id));

        try {
            Article article = future.get(2, TimeUnit.SECONDS); // Timeout de 2 secondes
            return ResponseEntity.ok(article);
        } finally {
            executor.shutdownNow();
        }
    }

    @Operation(
        summary = "Créer un nouvel article",
        description = "Crée un nouvel article dans le système et l'associe à un auteur existant. L'article sera immédiatement disponible pour consultation."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Article créé avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Auteur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<Article> createArticle(
            @Parameter(name = "title", description = "Titre de l'article") @RequestParam String title,
            @Parameter(name = "content", description = "Contenu de l'article") @RequestParam String content,
            @Parameter(name = "authorId", description = "ID de l'auteur") @RequestParam Long authorId) throws AuthorNotFoundException {
        Article article = articleService.createArticle(title, content, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(article);
    }

    @Operation(
        summary = "Mettre à jour un article",
        description = "Met à jour le contenu d'un article existant. Les modifications sont appliquées immédiatement et conservent la référence à l'auteur original."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Article mis à jour avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "404", description = "Article non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(
            @Parameter(name = "id", description = "ID de l'article à mettre à jour") @PathVariable Long id,
            @Parameter(name = "title", description = "Nouveau titre de l'article") @RequestParam String title,
            @Parameter(name = "content", description = "Nouveau contenu de l'article") @RequestParam String content) throws ArticleNotFoundException {
        Article article = articleService.updateArticle(id, title, content);
        return ResponseEntity.ok(article);
    }

    @Operation(
        summary = "Supprimer un article",
        description = "Supprime définitivement un article du système. Cette action ne supprime pas l'auteur associé à l'article."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Article supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Article non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(
            @Parameter(name = "id", description = "ID de l'article à supprimer") @PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
