package fr.eletutour.asgard.baldr.controller;

import fr.eletutour.asgard.baldr.exception.AuthorNotFoundException;
import fr.eletutour.asgard.baldr.model.Author;
import fr.eletutour.asgard.baldr.service.AuthorService;
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

@RestController
@RequestMapping("/authors")
@Tag(name = "Authors", description = """
    API de gestion des auteurs permettant de :
    - Lister tous les auteurs
    - Récupérer un auteur par son ID
    - Créer un nouvel auteur
    - Mettre à jour les informations d'un auteur
    - Supprimer un auteur
    - Cloner un auteur existant
    
    Chaque auteur possède un nom, une biographie et peut être associé à plusieurs articles.
    """)
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(
        summary = "Récupérer tous les auteurs",
        description = "Récupère la liste complète des auteurs disponibles dans le système. Retourne une liste vide si aucun auteur n'est trouvé."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des auteurs récupérée avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "404", description = "Auteurs non trouvés"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<?> getAuthors() {
        try {
            List<Author> authors = authorService.getAllAuthors();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Unexpected error retrieving authors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(
        summary = "Récupérer un auteur par ID",
        description = "Récupère les informations détaillées d'un auteur spécifique en utilisant son identifiant unique. Inclut son nom, sa biographie et la liste de ses articles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Auteur trouvé",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "404", description = "Auteur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public Author getAuthorById(@Parameter(name = "id", description = "ID de l'auteur à récupérer")
                                    @PathVariable("id") Long id) throws AuthorNotFoundException {
        return authorService.getAuthorById(id);
    }

    @Operation(
        summary = "Créer un nouvel auteur",
        description = "Crée un nouvel auteur dans le système avec les informations fournies. L'auteur sera immédiatement disponible pour être associé à des articles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Auteur créé avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<Author> createAuthor(
            @Parameter(name = "name", description = "Nom de l'auteur") @RequestParam String name,
            @Parameter(name = "bio", description = "Biographie de l'auteur") @RequestParam String bio) {
        Author author = authorService.createAuthor(name, bio);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @Operation(
        summary = "Mettre à jour un auteur",
        description = "Met à jour les informations d'un auteur existant. Toutes les modifications sont appliquées immédiatement et affectent tous les articles associés."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Auteur mis à jour avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "404", description = "Auteur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(
            @Parameter(name = "id", description = "ID de l'auteur à mettre à jour") @PathVariable Long id,
            @Parameter(name = "name", description = "Nouveau nom de l'auteur") @RequestParam String name,
            @Parameter(name = "bio", description = "Nouvelle biographie de l'auteur") @RequestParam String bio) throws AuthorNotFoundException {
        Author author = authorService.updateAuthor(id, name, bio);
        return ResponseEntity.ok(author);
    }

    @Operation(
        summary = "Supprimer un auteur",
        description = "Supprime définitivement un auteur du système. Cette action supprime également tous les articles associés à cet auteur."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Auteur supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Auteur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(name = "id", description = "ID de l'auteur à supprimer") @PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
