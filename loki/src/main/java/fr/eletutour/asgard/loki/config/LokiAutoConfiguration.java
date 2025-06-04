package fr.eletutour.asgard.loki.config;

import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.Munin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ConditionalOnProperty(name = "loki.enabled", havingValue = "true", matchIfMissing = false)
public class LokiAutoConfiguration {

    @Bean
    public LokiChaos lokiChaos() {
        LokiChaos chaos = new LokiChaos();
        chaos.setEnabled(false);
        chaos.setWatcher(new Hugin());
        chaos.setChaosType(new Munin());
        return chaos;
    }
} 