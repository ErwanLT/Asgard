package fr.eletutour.loki.chaos.configuration;

import fr.eletutour.asgard.core.JoinPointAwareChaosRule;
import fr.eletutour.loki.chaos.aspect.LokiAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnClass(LokiAspect.class)
public class LokiAutoConfiguration {

    @Bean
    public LokiAspect lokiAspect(List<JoinPointAwareChaosRule> rules) {
        return new LokiAspect(rules);
    }
}
