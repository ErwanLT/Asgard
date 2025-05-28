package fr.asgard.mimir.service;

import fr.asgard.mimir.annotation.ApiDescription;
import fr.asgard.mimir.config.MimirProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentationServiceTest {

    @TempDir
    Path tempDir;

    private DocumentationService documentationService;
    private UmlDiagramService umlDiagramService;
    private MimirProperties properties;

    @BeforeEach
    void setUp() {
        properties = new MimirProperties();
        properties.getDocumentation().setOutputDir(tempDir.toString());
        umlDiagramService = new UmlDiagramService(properties);
        documentationService = new DocumentationService(properties, umlDiagramService);
    }

    @Test
    void shouldGenerateDocumentationForClass() throws IOException, URISyntaxException {
        // Given
        @ApiDescription(
            value = "Test API description",
            tags = {"test", "api"},
            category = "test-category"
        )
        class TestClass {
            @ApiDescription("Test method description")
            public void testMethod() {}
        }

        // When
        documentationService.generateApiDocumentation(TestClass.class);

        // Then
        Path docFile = tempDir.resolve("testclass.md");
        assertThat(docFile).exists();
        
        // Lire le fichier généré
        String generatedContent = Files.readString(docFile);
        
        // Lire le fichier de référence
        String referenceContent = Files.readString(
            Path.of(getClass().getResource("/reference/testclass.md").toURI())
        );

        // Normaliser les contenus
        String normalizedGenerated = generatedContent.lines()
            .map(String::trim)
            .collect(Collectors.joining("\n"))
            .trim();
        String normalizedReference = referenceContent.lines()
            .map(String::trim)
            .collect(Collectors.joining("\n"))
            .trim();

        // Comparer les contenus
        assertThat(normalizedGenerated)
            .as("Le contenu généré devrait correspondre au fichier de référence")
            .isEqualTo(normalizedReference);
    }

    @Test
    void shouldGenerateDocumentationForWellDocumentedClass() throws IOException, URISyntaxException {
        // Given
        @ApiDescription(
            value = "Contrôleur REST pour la gestion des utilisateurs. Ce contrôleur permet de créer, récupérer, mettre à jour et supprimer des utilisateurs.",
            tags = {"user", "rest", "api", "crud"},
            category = "user-management"
        )
        class UserController {
            @ApiDescription(
                value = "Crée un nouvel utilisateur dans le système. L'utilisateur doit fournir un email valide et un mot de passe respectant les critères de sécurité.",
                returnType = "L'utilisateur créé avec son identifiant unique",
                throws_ = {
                    @ApiDescription.Throws(
                        exception = InvalidEmailException.class,
                        description = "L'email fourni n'est pas valide"
                    ),
                    @ApiDescription.Throws(
                        exception = InvalidPasswordException.class,
                        description = "Le mot de passe ne respecte pas les critères de sécurité"
                    ),
                    @ApiDescription.Throws(
                        exception = UserAlreadyExistsException.class,
                        description = "Un utilisateur avec cet email existe déjà"
                    )
                }
            )
            public void createUser(@ApiDescription("userDTO : Données de l'utilisateur à créer, incluant email et mot de passe") UserDTO userDTO) {}

            @ApiDescription(
                value = "Récupère les informations d'un utilisateur à partir de son identifiant unique. Retourne une erreur 404 si l'utilisateur n'existe pas.",
                returnType = "Les informations complètes de l'utilisateur",
                throws_ = {
                    @ApiDescription.Throws(
                        exception = UserNotFoundException.class,
                        description = "L'utilisateur n'a pas été trouvé"
                    )
                }
            )
            public void getUserById(@ApiDescription("id : Identifiant unique de l'utilisateur") String id) {}

            @ApiDescription(
                value = "Met à jour les informations d'un utilisateur existant. Seuls les champs fournis seront mis à jour, les autres resteront inchangés.",
                returnType = "L'utilisateur mis à jour",
                throws_ = {
                    @ApiDescription.Throws(
                        exception = UserNotFoundException.class,
                        description = "L'utilisateur n'a pas été trouvé"
                    ),
                    @ApiDescription.Throws(
                        exception = InvalidEmailException.class,
                        description = "Le nouvel email n'est pas valide"
                    )
                }
            )
            public void updateUser(
                @ApiDescription("id : Identifiant unique de l'utilisateur") String id,
                @ApiDescription("userDTO : Données à mettre à jour") UserDTO userDTO
            ) {}

            @ApiDescription(
                value = "Supprime un utilisateur du système. Cette action est irréversible et supprimera toutes les données associées à l'utilisateur.",
                returnType = "Confirmation de la suppression",
                throws_ = {
                    @ApiDescription.Throws(
                        exception = UserNotFoundException.class,
                        description = "L'utilisateur n'a pas été trouvé"
                    ),
                    @ApiDescription.Throws(
                        exception = UserDeletionException.class,
                        description = "Impossible de supprimer l'utilisateur"
                    )
                }
            )
            public void deleteUser(@ApiDescription("id : Identifiant unique de l'utilisateur") String id) {}
        }

        // When
        documentationService.generateApiDocumentation(UserController.class);

        // Then
        Path docFile = tempDir.resolve("usercontroller.md");
        assertThat(docFile).exists();
        
        // Lire le fichier généré
        String generatedContent = Files.readString(docFile);
        
        // Lire le fichier de référence
        String referenceContent = Files.readString(
            Path.of(getClass().getResource("/reference/usercontroller-advanced.md").toURI())
        );

        // Normaliser les contenus
        String normalizedGenerated = generatedContent.lines()
            .map(String::trim)
            .collect(Collectors.joining("\n"))
            .trim();
        String normalizedReference = referenceContent.lines()
            .map(String::trim)
            .collect(Collectors.joining("\n"))
            .trim();

        // Comparer les contenus
        assertThat(normalizedGenerated)
            .as("Le contenu généré devrait correspondre au fichier de référence pour une classe bien documentée")
            .isEqualTo(normalizedReference);
    }

    @Test
    void shouldNotGenerateDocumentationForClassWithoutAnnotation() throws IOException {
        // Given
        class TestClass {
            public void testMethod() {}
        }

        // When
        documentationService.generateApiDocumentation(TestClass.class);

        // Then
        Path docFile = tempDir.resolve("testclass.md");
        assertThat(docFile).doesNotExist();
    }

    @Test
    void shouldHandleClassWithOnlyMethodAnnotations() throws IOException {
        // Given
        class TestClass {
            @ApiDescription("Test method description")
            public void testMethod() {}
        }

        // When
        documentationService.generateApiDocumentation(TestClass.class);

        // Then
        Path docFile = tempDir.resolve("testclass.md");
        assertThat(docFile).doesNotExist();
    }

    // Classes d'exception pour les tests
    private static class UserDTO {}
    private static class InvalidEmailException extends Exception {}
    private static class InvalidPasswordException extends Exception {}
    private static class UserAlreadyExistsException extends Exception {}
    private static class UserNotFoundException extends Exception {}
    private static class UserDeletionException extends Exception {}
} 