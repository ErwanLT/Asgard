package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.config.MimirProperties;
import fr.eletutour.asgard.mimir.model.Documentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DocumentationServiceTest {

    @Mock
    private MimirProperties properties;

    @Mock
    private UmlDiagramService umlDiagramService;

    @Mock
    private SearchService searchService;

    @TempDir
    Path tempDir;

    private DocumentationService documentationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(properties.getOutputPath()).thenReturn(tempDir);
        documentationService = new DocumentationService(properties, umlDiagramService, searchService);
    }

    @Test
    void generateDocumentation_ShouldReturnNull_WhenNoTagAnnotation() {
        // Arrange
        class TestClass {}

        // Act
        Documentation documentation = documentationService.generateDocumentation(TestClass.class);

        // Assert
        assertNull(documentation);
        verify(umlDiagramService, never()).generateClassDiagram(any());
        verify(searchService, never()).saveDocumentation(any());
    }

    @Test
    void generatePackageDocumentation_ShouldGenerateDocumentationForAllClasses() throws ClassNotFoundException {
        // Arrange
        @Tag(name = "Test1", description = "Test Description 1")
        class TestClass1 {}

        @Tag(name = "Test2", description = "Test Description 2")
        class TestClass2 {}

        when(umlDiagramService.generateClassDiagram(any())).thenReturn("classDiagram\nclass TestClass");
        when(searchService.saveDocumentation(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        List<Documentation> documentations = documentationService.generatePackageDocumentation(
            TestClass1.class.getPackage().getName());

        // Assert
        assertNotNull(documentations);
        assertFalse(documentations.isEmpty());
        verify(umlDiagramService, atLeastOnce()).generateClassDiagram(any());
        verify(searchService, atLeastOnce()).saveDocumentation(any());
    }

    @Test
    void getDocumentation_ShouldReturnDocumentation() {
        // Arrange
        String className = "TestClass";
        Documentation expectedDoc = new Documentation();
        expectedDoc.setTitle(className);
        when(searchService.findDocumentationByTitle(className)).thenReturn(expectedDoc);

        // Act
        Documentation documentation = documentationService.getDocumentation(className);

        // Assert
        assertNotNull(documentation);
        assertEquals(className, documentation.getTitle());
        verify(searchService).findDocumentationByTitle(className);
    }
} 