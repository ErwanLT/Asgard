package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.config.MimirProperties;
import fr.eletutour.asgard.mimir.model.Documentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentationServiceTest {

    @Mock
    private MimirProperties properties;

    @Mock
    private UmlDiagramService umlDiagramService;

    @TempDir
    Path tempDir;

    private DocumentationService documentationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(properties.getOutputPath()).thenReturn(tempDir);
        documentationService = new DocumentationService(properties, umlDiagramService);
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
    }

    @Test
    void generatePackageDocumentation_ShouldGenerateDocumentationForAllClasses() throws ClassNotFoundException {
        // Arrange
        @Tag(name = "Test1", description = "Test Description 1")
        class TestClass1 {}

        @Tag(name = "Test2", description = "Test Description 2")
        class TestClass2 {}

        when(umlDiagramService.generateClassDiagram(any())).thenReturn("classDiagram\nclass TestClass");

        // Act
        List<Documentation> documentations = documentationService.generatePackageDocumentation(
            TestClass1.class.getPackage().getName());

        // Assert
        assertNotNull(documentations);
        assertFalse(documentations.isEmpty());
        verify(umlDiagramService, atLeastOnce()).generateClassDiagram(any());
    }
} 