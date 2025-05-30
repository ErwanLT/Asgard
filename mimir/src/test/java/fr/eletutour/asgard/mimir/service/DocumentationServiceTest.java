package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.annotation.ApiDescription;
import fr.eletutour.asgard.mimir.config.MimirProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentationServiceTest {

    @Mock
    private MimirProperties mimirProperties;

    @Mock
    private UmlDiagramService umlDiagramService;

    @InjectMocks
    private DocumentationService documentationService;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("mimir-test");
    }

    @Test
    void shouldGenerateDocumentation() throws IOException {
        // Given
        when(mimirProperties.getOutputPath()).thenReturn(tempDir);
        @ApiDescription(
            value = "Test class description",
            tags = {"test", "documentation"},
            category = "Test"
        )
        class TestClass {
            @ApiDescription(
                value = "Test method description",
                returnType = "void",
                throws_ = {
                    @ApiDescription.Throws(
                        exception = RuntimeException.class,
                        description = "If something goes wrong"
                    )
                }
            )
            public void testMethod() {}
        }

        // When
        documentationService.generateDocumentation(TestClass.class);

        // Then
        Path expectedFile = tempDir.resolve("testclass.md");
        assertThat(expectedFile).exists();
        String content = Files.readString(expectedFile);
        assertThat(content)
            .contains("# TestClass")
            .contains("Test class description")
            .contains("Tags: `test`, `documentation`")
            .contains("Category: Test")
            .contains("## Methods")
            .contains("### testMethod")
            .contains("Test method description")
            .contains("#### Returns")
            .contains("void")
            .contains("#### Throws")
            .contains("`RuntimeException` : If something goes wrong");

        verify(umlDiagramService).generateClassDiagram(eq(TestClass.class));
    }

    @Test
    void shouldGenerateDocumentationForClass() throws IOException, URISyntaxException {
        // Given
        when(mimirProperties.getOutputPath()).thenReturn(tempDir);
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
        documentationService.generateDocumentation(TestClass.class);

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

        verify(umlDiagramService).generateClassDiagram(eq(TestClass.class));
    }

    @Test
    void shouldGenerateDocumentationForWellDocumentedClass() throws IOException, URISyntaxException {
        // Given
        when(mimirProperties.getOutputPath()).thenReturn(tempDir);
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
        documentationService.generateDocumentation(UserController.class);

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
        documentationService.generateDocumentation(TestClass.class);

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
        documentationService.generateDocumentation(TestClass.class);

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