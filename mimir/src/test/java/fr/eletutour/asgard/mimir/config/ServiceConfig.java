package fr.eletutour.asgard.mimir.config;

import fr.eletutour.asgard.mimir.service.DocumentationService;
import fr.eletutour.asgard.mimir.service.UmlDiagramService;
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
    public DocumentationService documentationService() {
        return new DocumentationService(mimirProperties(), null);
    }

    @Bean
    @Primary
    public UmlDiagramService umlDiagramService() {
        return new UmlDiagramService(mimirProperties());
    }
} 