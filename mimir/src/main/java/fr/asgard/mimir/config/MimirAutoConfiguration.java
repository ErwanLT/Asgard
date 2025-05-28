package fr.asgard.mimir.config;

import fr.asgard.mimir.service.DocumentationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MimirProperties.class)
public class MimirAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DocumentationService documentationService(MimirProperties properties) {
        return new DocumentationService(properties);
    }
} 