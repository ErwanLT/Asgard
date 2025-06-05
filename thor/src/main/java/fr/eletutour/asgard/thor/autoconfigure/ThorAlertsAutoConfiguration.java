package fr.eletutour.asgard.thor.autoconfigure;

import fr.eletutour.asgard.thor.alerts.AlertService;
import fr.eletutour.asgard.thor.alerts.DefaultAlertService;
import fr.eletutour.asgard.thor.config.ThorProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

@AutoConfiguration
@EnableConfigurationProperties(ThorProperties.class)
@EnableScheduling
@ConditionalOnProperty(prefix = "thor.alerts", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ThorAlertsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ThorAlertsAutoConfiguration.class);

    private final MeterRegistry meterRegistry;
    private final ThorProperties thorProperties;
    private final AlertService alertService; // Will be injected by Spring

    // Constructor for required dependencies for the class instance itself
    // AlertService will be injected into the field after the bean is created.
    public ThorAlertsAutoConfiguration(MeterRegistry meterRegistry, ThorProperties thorProperties, AlertService alertService) {
        this.meterRegistry = meterRegistry;
        this.thorProperties = thorProperties;
        this.alertService = alertService;
    }

    // Bean definition for the AlertService
    @Bean
    public AlertService defaultAlertService(ThorProperties properties) {
        // Pass ThorProperties to the service if it needs it directly for bean creation
        // Or if DefaultAlertService is a simple @Service, it can @Autowire ThorProperties itself.
        // Here, passing it explicitly is fine.
        return new DefaultAlertService(properties);
    }

    @Scheduled(fixedRateString = "#{thorProperties.metrics.collection.interval ?: '60s'}")
    public void scheduledAlertCheck() {
        // Use the injected alertService field
        String intervalProperty = this.thorProperties.getMetrics().getCollection().getInterval();
        // Validate interval string to prevent issues with @Scheduled fixedRateString if it's not a valid duration
        // For simplicity, this basic check looks for non-empty and ending with 's', 'm', 'h', 'd'.
        // Spring's SpEL for @Scheduled is powerful but can throw exceptions if the SpEL expression itself is invalid
        // or returns a type that cannot be converted to long (for fixedRate/fixedDelay).
        // Here, we rely on ThorProperties having a default that is valid.
        if (!StringUtils.hasText(intervalProperty) || !intervalProperty.matches("\\d+[smhd]")) {
            log.warn("Invalid or empty 'thor.metrics.collection.interval' (value: '{}') for alert scheduling. Defaulting to 1 minute for this check, but please configure it correctly.", intervalProperty);
            // See previous comment about SpEL evaluation and potential startup failure.
        }
        log.debug("Executing scheduled alert check. Interval configured via thor.metrics.collection.interval: {}", intervalProperty);
        this.alertService.checkForAlerts(this.meterRegistry);
    }
}
