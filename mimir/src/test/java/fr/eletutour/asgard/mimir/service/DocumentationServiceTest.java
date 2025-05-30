package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.config.MimirProperties;
import fr.eletutour.asgard.mimir.model.Documentation;
import fr.eletutour.asgard.mimir.util.ClassFinder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentationServiceTest {

    @Mock
    private MimirProperties mimirProperties;

    @Mock
    private UmlDiagramService umlDiagramService;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private DocumentationService documentationService;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("mimir-test");
    }

    @Test
    void shouldGeneratePackageDocumentation() throws IOException, ClassNotFoundException {
        // Given
        when(mimirProperties.getOutputPath()).thenReturn(tempDir);

        @Tag(name = "Test1", description = "Test class 1 description")
        class TestClass1 {
            @Operation(
                summary = "Test method 1",
                description = "Test method 1 description"
            )
            public void testMethod1() {}
        }

        @Tag(name = "Test2", description = "Test class 2 description")
        class TestClass2 {
            @Operation(
                summary = "Test method 2",
                description = "Test method 2 description"
            )
            public void testMethod2() {}
        }

        try (MockedStatic<ClassFinder> classFinderMock = mockStatic(ClassFinder.class)) {
            classFinderMock.when(() -> ClassFinder.findClassesInPackage("test.package"))
                .thenReturn(Arrays.asList(TestClass1.class, TestClass2.class));

            // When
            List<Documentation> documentations = documentationService.generatePackageDocumentation("test.package");

            // Then
            assertThat(documentations).hasSize(2);
            assertThat(documentations)
                .extracting(Documentation::getTitle)
                .containsExactlyInAnyOrder("TestClass1", "TestClass2");

            Path expectedFile1 = tempDir.resolve("testclass1.md");
            Path expectedFile2 = tempDir.resolve("testclass2.md");
            assertThat(expectedFile1).exists();
            assertThat(expectedFile2).exists();

            verify(umlDiagramService).generateClassDiagram(eq(TestClass1.class));
            verify(umlDiagramService).generateClassDiagram(eq(TestClass2.class));
            verify(searchService, times(2)).saveDocumentation(any(Documentation.class));
        }
    }

    @Test
    void shouldGenerateDocumentationForClass() throws IOException, URISyntaxException {
        // Given
        when(mimirProperties.getOutputPath()).thenReturn(tempDir);
        @Tag(name = "Test", description = "Test API description")
        class TestClass {
            @Operation(
                summary = "Test method",
                description = "Test method description"
            )
            public void testMethod() {}
        }

        // When
        Documentation documentation = documentationService.generateDocumentation(TestClass.class);

        // Then
        assertThat(documentation).isNotNull();
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
        verify(searchService).saveDocumentation(documentation);
    }

    @Test
    void shouldGenerateDocumentationForWellDocumentedClass() throws IOException, URISyntaxException {
        // Given
        when(mimirProperties.getOutputPath()).thenReturn(tempDir);
        @Tag(name = "User", description = "Contrôleur REST pour la gestion des utilisateurs. Ce contrôleur permet de créer, récupérer, mettre à jour et supprimer des utilisateurs.")
        class UserController {
            @Operation(
                summary = "Créer un utilisateur",
                description = "Crée un nouvel utilisateur dans le système. L'utilisateur doit fournir un email valide et un mot de passe respectant les critères de sécurité."
            )
            @ApiResponse(responseCode = "200", description = "L'utilisateur créé avec son identifiant unique")
            @ApiResponse(responseCode = "400", description = "L'email fourni n'est pas valide")
            @ApiResponse(responseCode = "400", description = "Le mot de passe ne respecte pas les critères de sécurité")
            @ApiResponse(responseCode = "409", description = "Un utilisateur avec cet email existe déjà")
            public void createUser(
                @Parameter(description = "Données de l'utilisateur à créer, incluant email et mot de passe")
                UserDTO userDTO
            ) {}

            @Operation(
                summary = "Récupérer un utilisateur",
                description = "Récupère les informations d'un utilisateur à partir de son identifiant unique. Retourne une erreur 404 si l'utilisateur n'existe pas."
            )
            @ApiResponse(responseCode = "200", description = "Les informations complètes de l'utilisateur")
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas été trouvé")
            public void getUserById(
                @Parameter(description = "Identifiant unique de l'utilisateur")
                String id
            ) {}

            @Operation(
                summary = "Mettre à jour un utilisateur",
                description = "Met à jour les informations d'un utilisateur existant. Seuls les champs fournis seront mis à jour, les autres resteront inchangés."
            )
            @ApiResponse(responseCode = "200", description = "L'utilisateur mis à jour")
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas été trouvé")
            @ApiResponse(responseCode = "400", description = "Le nouvel email n'est pas valide")
            public void updateUser(
                @Parameter(description = "Identifiant unique de l'utilisateur")
                String id,
                @Parameter(description = "Données à mettre à jour")
                UserDTO userDTO
            ) {}

            @Operation(
                summary = "Supprimer un utilisateur",
                description = "Supprime un utilisateur du système. Cette action est irréversible et supprimera toutes les données associées à l'utilisateur."
            )
            @ApiResponse(responseCode = "200", description = "Confirmation de la suppression")
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas été trouvé")
            @ApiResponse(responseCode = "500", description = "Impossible de supprimer l'utilisateur")
            public void deleteUser(
                @Parameter(description = "Identifiant unique de l'utilisateur")
                String id
            ) {}
        }

        // When
        Documentation documentation = documentationService.generateDocumentation(UserController.class);

        // Then
        assertThat(documentation).isNotNull();
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

        verify(umlDiagramService).generateClassDiagram(eq(UserController.class));
        verify(searchService).saveDocumentation(documentation);
    }

    @Test
    void shouldNotGenerateDocumentationForClassWithoutAnnotation() throws IOException {
        // Given
        class TestClass {
            public void testMethod() {}
        }

        // When
        Documentation documentation = documentationService.generateDocumentation(TestClass.class);

        // Then
        assertThat(documentation).isNull();
        Path docFile = tempDir.resolve("testclass.md");
        assertThat(docFile).doesNotExist();
        verifyNoInteractions(umlDiagramService, searchService);
    }

    @Test
    void shouldHandleClassWithOnlyMethodAnnotations() throws IOException {
        // Given
        class TestClass {
            @Operation(
                summary = "Test method",
                description = "Test method description"
            )
            public void testMethod() {}
        }

        // When
        Documentation documentation = documentationService.generateDocumentation(TestClass.class);

        // Then
        assertThat(documentation).isNull();
        Path docFile = tempDir.resolve("testclass.md");
        assertThat(docFile).doesNotExist();
        verifyNoInteractions(umlDiagramService, searchService);
    }

    // Classes d'exception pour les tests
    private static class UserDTO {}
} 