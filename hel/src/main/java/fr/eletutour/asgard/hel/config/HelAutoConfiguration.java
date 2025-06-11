package fr.eletutour.asgard.hel.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("fr.eletutour.asgard.hel")
@Import({SchedulingConfig.class})
public class HelAutoConfiguration {
} 