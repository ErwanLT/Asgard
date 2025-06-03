package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.model.Documentation;
import fr.eletutour.asgard.mimir.repository.DocumentationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    @Mock
    private DocumentationRepository documentationRepository;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchService = new SearchService(documentationRepository);
    }

    @Test
    void saveDocumentation_ShouldSaveAndReturnDocumentation() {
        // Arrange
        Documentation documentation = new Documentation();
        documentation.setTitle("Test Doc");
        documentation.setContent("Test Content");
        when(documentationRepository.save(any(Documentation.class))).thenReturn(documentation);

        // Act
        Documentation savedDoc = searchService.saveDocumentation(documentation);

        // Assert
        assertNotNull(savedDoc);
        assertEquals("Test Doc", savedDoc.getTitle());
        verify(documentationRepository, times(1)).save(documentation);
    }

    @Test
    void findDocumentationById_ShouldReturnDocumentation() {
        // Arrange
        String id = "test-id";
        Documentation documentation = new Documentation();
        documentation.setTitle("Test Doc");
        when(documentationRepository.findById(id)).thenReturn(java.util.Optional.of(documentation));

        // Act
        Documentation foundDoc = searchService.findDocumentationById(id);

        // Assert
        assertNotNull(foundDoc);
        assertEquals("Test Doc", foundDoc.getTitle());
        verify(documentationRepository, times(1)).findById(id);
    }

    @Test
    void findDocumentationById_ShouldReturnNull_WhenNotFound() {
        // Arrange
        String id = "non-existent-id";
        when(documentationRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // Act
        Documentation foundDoc = searchService.findDocumentationById(id);

        // Assert
        assertNull(foundDoc);
        verify(documentationRepository, times(1)).findById(id);
    }

    @Test
    void deleteDocumentation_ShouldCallRepository() {
        // Arrange
        String id = "test-id";

        // Act
        searchService.deleteDocumentation(id);

        // Assert
        verify(documentationRepository, times(1)).deleteById(id);
    }

    @Test
    void findDocumentationByTitle_ShouldReturnDocumentation() {
        // Arrange
        String title = "Test Doc";
        Documentation documentation = new Documentation();
        documentation.setTitle(title);
        when(documentationRepository.findByTitle(title)).thenReturn(documentation);

        // Act
        Documentation foundDoc = searchService.findDocumentationByTitle(title);

        // Assert
        assertNotNull(foundDoc);
        assertEquals(title, foundDoc.getTitle());
        verify(documentationRepository, times(1)).findByTitle(title);
    }
} 