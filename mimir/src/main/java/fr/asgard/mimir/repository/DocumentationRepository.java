package fr.asgard.mimir.repository;

import fr.asgard.mimir.model.DocumentationEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentationRepository extends ElasticsearchRepository<DocumentationEntry, String> {
    
    @Query("""
        {
            "multi_match": {
                "query": "?0",
                "fields": ["content^3", "title^2", "tags^1.5", "category^1"],
                "type": "best_fields"
            }
        }
    """)
    Page<DocumentationEntry> searchByContent(String query, Pageable pageable);

    List<DocumentationEntry> findByTagsIn(List<String> tags);

    List<DocumentationEntry> findByCategory(String category);

    @Query("""
        {
            "aggs": {
                "unique_tags": {
                    "terms": {
                        "field": "tags.keyword",
                        "size": 100
                    }
                }
            }
        }
    """)
    List<String> findAllTags();

    @Query("""
        {
            "aggs": {
                "unique_categories": {
                    "terms": {
                        "field": "category.keyword",
                        "size": 100
                    }
                }
            }
        }
    """)
    List<String> findAllCategories();
} 