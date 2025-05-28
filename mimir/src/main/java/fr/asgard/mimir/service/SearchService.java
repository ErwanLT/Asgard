package fr.asgard.mimir.service;

import fr.asgard.mimir.model.DocumentationEntry;
import fr.asgard.mimir.repository.DocumentationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final DocumentationRepository documentationRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public Page<DocumentationEntry> search(String query, Pageable pageable) {
        log.info("Recherche de documentation avec la requête: {}", query);
        return documentationRepository.searchByContent(query, pageable);
    }

    public List<DocumentationEntry> searchByTags(List<String> tags) {
        log.info("Recherche de documentation par tags: {}", tags);
        return documentationRepository.findByTagsIn(tags);
    }

    public List<DocumentationEntry> searchByCategory(String category) {
        log.info("Recherche de documentation par catégorie: {}", category);
        return documentationRepository.findByCategory(category);
    }

    public List<DocumentationEntry> getSuggestions(String query) {
        log.info("Génération de suggestions pour la requête: {}", query);
        
        String searchQuery = String.format("""
            {
                "multi_match": {
                    "query": "%s",
                    "fields": ["content^3", "title^2", "tags^1.5", "category^1"],
                    "type": "best_fields"
                }
            }
            """, query);
        
        Query elasticQuery = new StringQuery(searchQuery);
        SearchHits<DocumentationEntry> searchHits = elasticsearchOperations.search(elasticQuery, DocumentationEntry.class);
        
        return searchHits.getSearchHits().stream()
            .map(hit -> hit.getContent())
            .collect(Collectors.toList());
    }

    public List<String> getAllTags() {
        return documentationRepository.findAllTags();
    }

    public List<String> getAllCategories() {
        return documentationRepository.findAllCategories();
    }
} 