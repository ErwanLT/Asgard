package fr.eletutour.asgard.heimdall.configuration;

import fr.eletutour.asgard.heimdall.aspect.ChaosMetricsAspect;
import fr.eletutour.asgard.heimdall.metrics.ChaosMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ChaosMetricsService.class)
public class HeimdallAutoConfiguration {

    @Bean
    public ChaosMetricsService chaosMetricsService(MeterRegistry meterRegistry) {
        return new ChaosMetricsService(meterRegistry);
    }

    @Bean
    public ChaosMetricsAspect chaosMetricsAspect(ChaosMetricsService metricsService) {
        return new ChaosMetricsAspect(metricsService);
    }
} 