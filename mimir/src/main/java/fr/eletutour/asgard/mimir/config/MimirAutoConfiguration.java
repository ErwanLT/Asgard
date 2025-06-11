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

    private final MimirProperties properties;

    public MimirAutoConfiguration(MimirProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean
    public UmlDiagramService umlDiagramService() {
        return new UmlDiagramService();
    }


    @Bean
    @ConditionalOnMissingBean
    public DocumentationService documentationService(
            MimirProperties properties,
            UmlDiagramService umlDiagramService) {
        return new DocumentationService(properties, umlDiagramService);
    }
} 