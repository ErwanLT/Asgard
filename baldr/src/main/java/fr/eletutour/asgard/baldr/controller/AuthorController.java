package fr.eletutour.asgard.baldr.controller;

import fr.eletutour.asgard.baldr.exception.ArticleNotFoundException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authors")
@Tag(name = "Authors", description = "API de gestion des auteurs")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Récupérer tous les auteurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des auteurs récupérée avec succès",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "404", description = "Auteurs non trouvés")
    })
    @GetMapping
    public List<Author> getAuthors() throws AuthorNotFoundException, ArticleNotFoundException {
        return authorService.getAuthors();
    }

    @Operation(summary = "Récupérer un auteur par ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Auteur trouvé",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "404", description = "Auteur non trouvé")
    })
    @GetMapping("/{id}")
    public Author getAuthorById(@Parameter(name = "id", description = "ID de l'auteur à récupérer") @PathVariable Long id) throws AuthorNotFoundException {
        return authorService.getAuthorById(id);
    }
}
