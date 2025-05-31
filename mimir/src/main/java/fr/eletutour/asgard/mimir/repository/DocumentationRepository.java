package fr.eletutour.asgard.mimir.repository;

import fr.eletutour.asgard.mimir.model.Documentation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentationRepository extends ElasticsearchRepository<Documentation, String> {
    Documentation findByTitle(String title);
}
