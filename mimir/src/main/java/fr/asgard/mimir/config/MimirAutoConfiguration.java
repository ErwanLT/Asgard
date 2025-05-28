package fr.asgard.mimir.config;

import fr.asgard.mimir.repository.DocumentationRepository;
import fr.asgard.mimir.service.DocumentationService;
import fr.asgard.mimir.service.SearchService;
import fr.asgard.mimir.service.UmlDiagramService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
@EnableConfigurationProperties(MimirProperties.class)
public class MimirAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UmlDiagramService umlDiagramService(MimirProperties properties) {
        return new UmlDiagramService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchService searchService(DocumentationRepository documentationRepository, ElasticsearchOperations elasticsearchOperations) {
        return new SearchService(documentationRepository, elasticsearchOperations);
    }

    @Bean
    @ConditionalOnMissingBean
    public DocumentationService documentationService(
            MimirProperties properties,
            UmlDiagramService umlDiagramService,
            SearchService searchService) {
        return new DocumentationService(properties, umlDiagramService, searchService);
    }
} 