package fr.eletutour.asgard.mimir.config;


import fr.eletutour.asgard.mimir.repository.DocumentationRepository;
import fr.eletutour.asgard.mimir.service.DocumentationService;
import fr.eletutour.asgard.mimir.service.SearchService;
import fr.eletutour.asgard.mimir.service.UmlDiagramService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableConfigurationProperties(MimirProperties.class)
@EnableElasticsearchRepositories(basePackages = "fr.eletutour.asgard.mimir.repository")
public class MimirAutoConfiguration extends ElasticsearchConfiguration {

    private final MimirProperties properties;

    public MimirAutoConfiguration(MimirProperties properties) {
        this.properties = properties;
    }

    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(properties.getElasticsearch().getHost() + ":" + properties.getElasticsearch().getPort())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public UmlDiagramService umlDiagramService() {
        return new UmlDiagramService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchService searchService(DocumentationRepository documentationRepository) {
        return new SearchService(documentationRepository);
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