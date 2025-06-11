package fr.eletutour.asgard.hel.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("fr.eletutour.asgard.hel")
@Import({SchedulingConfig.class})
@ConditionalOnProperty(name = "hel.enabled", havingValue = "true", matchIfMissing = false)
public class HelAutoConfiguration {
} 