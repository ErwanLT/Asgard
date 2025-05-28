package fr.asgard.mimir.config;

import fr.asgard.mimir.service.DocumentationService;
import fr.asgard.mimir.service.SearchService;
import fr.asgard.mimir.service.UmlDiagramService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfig {

    @Bean
    @Primary
    public MimirProperties mimirProperties() {
        MimirProperties properties = new MimirProperties();
        properties.setOutputPath(java.nio.file.Paths.get("target/test-output"));
        return properties;
    }

    @Bean
    @Primary
    public SearchService searchService() {
        return new SearchService(null, null);
    }

    @Bean
    @Primary
    public DocumentationService documentationService() {
        return new DocumentationService(mimirProperties(), null, null);
    }

    @Bean
    @Primary
    public UmlDiagramService umlDiagramService() {
        return new UmlDiagramService(mimirProperties());
    }
} 