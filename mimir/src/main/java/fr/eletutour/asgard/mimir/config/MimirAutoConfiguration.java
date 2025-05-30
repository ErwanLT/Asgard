package fr.eletutour.asgard.mimir.config;


import fr.eletutour.asgard.mimir.service.DocumentationService;
import fr.eletutour.asgard.mimir.service.UmlDiagramService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public DocumentationService documentationService(
            MimirProperties properties,
            UmlDiagramService umlDiagramService) {
        return new DocumentationService(properties, umlDiagramService);
    }
} 