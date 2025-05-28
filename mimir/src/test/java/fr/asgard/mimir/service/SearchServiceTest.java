package fr.asgard.mimir.service;

import fr.asgard.mimir.model.DocumentationEntry;
import fr.asgard.mimir.repository.DocumentationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.query.Query;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private DocumentationRepository documentationRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private SearchService searchService;

    private DocumentationEntry entry1;
    private DocumentationEntry entry2;

    @BeforeEach
    void setUp() {
        entry1 = DocumentationEntry.builder()
                .id("1")
                .title("Test Documentation 1")
                .content("Contenu de test 1")
                .tags(Arrays.asList("test", "java"))
                .category("API")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entry2 = DocumentationEntry.builder()
                .id("2")
                .title("Test Documentation 2")
                .content("Contenu de test 2")
                .tags(Arrays.asList("test", "spring"))
                .category("Guide")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSearchByContent() {
        // Given
        String query = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocumentationEntry> expectedPage = new PageImpl<>(Arrays.asList(entry1, entry2));
        when(documentationRepository.searchByContent(query, pageable)).thenReturn(expectedPage);

        // When
        Page<DocumentationEntry> result = searchService.search(query, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(documentationRepository).searchByContent(query, pageable);
    }

    @Test
    void shouldSearchByTags() {
        // Given
        List<String> tags = Arrays.asList("test", "java");
        when(documentationRepository.findByTagsIn(tags)).thenReturn(Arrays.asList(entry1));

        // When
        List<DocumentationEntry> result = searchService.searchByTags(tags);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(entry1);
        verify(documentationRepository).findByTagsIn(tags);
    }

    @Test
    void shouldSearchByCategory() {
        // Given
        String category = "API";
        when(documentationRepository.findByCategory(category)).thenReturn(Arrays.asList(entry1));

        // When
        List<DocumentationEntry> result = searchService.searchByCategory(category);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(entry1);
        verify(documentationRepository).findByCategory(category);
    }

    @Test
    void shouldGetSuggestions() {
        // Given
        String query = "test";
        List<SearchHit<DocumentationEntry>> searchHits = Arrays.asList(
                new SearchHitImpl<>(entry1),
                new SearchHitImpl<>(entry2)
        );
        SearchHits<DocumentationEntry> searchHitsResult = new SearchHitsImpl<DocumentationEntry>(
            2L,
            TotalHitsRelation.EQUAL_TO,
            0f,
            Duration.ZERO,
            null,
            null,
            searchHits,
            null,
            null,
            null
        );
        when(elasticsearchOperations.search(any(Query.class), eq(DocumentationEntry.class)))
                .thenReturn(searchHitsResult);

        // When
        List<DocumentationEntry> suggestions = searchService.getSuggestions(query);

        // Then
        assertThat(suggestions).hasSize(2);
        assertThat(suggestions).containsExactly(entry1, entry2);
        verify(elasticsearchOperations).search(any(Query.class), eq(DocumentationEntry.class));
    }

    @Test
    void shouldGetAllTags() {
        // Given
        List<String> expectedTags = Arrays.asList("test", "java", "spring");
        when(documentationRepository.findAllTags()).thenReturn(expectedTags);

        // When
        List<String> tags = searchService.getAllTags();

        // Then
        assertThat(tags).isEqualTo(expectedTags);
        verify(documentationRepository).findAllTags();
    }

    @Test
    void shouldGetAllCategories() {
        // Given
        List<String> expectedCategories = Arrays.asList("API", "Guide");
        when(documentationRepository.findAllCategories()).thenReturn(expectedCategories);

        // When
        List<String> categories = searchService.getAllCategories();

        // Then
        assertThat(categories).isEqualTo(expectedCategories);
        verify(documentationRepository).findAllCategories();
    }

    private static class SearchHitImpl<T> extends SearchHit<T> {
        private final T content;

        public SearchHitImpl(T content) {
            super(null, null, null, 0f, null, null, null, null, null, null, content);
            this.content = content;
        }

        @Override
        public T getContent() {
            return content;
        }
    }
} 