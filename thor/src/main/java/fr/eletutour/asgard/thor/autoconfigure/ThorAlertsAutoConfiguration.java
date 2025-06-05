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

/**
 * Auto-configuration for Thor's alerting mechanism.
 * <p>
 * This class enables Spring's scheduling capabilities and sets up the {@link AlertService}
 * to periodically check for metric threshold breaches based on {@link ThorProperties}.
 * The entire alerting mechanism can be enabled or disabled via the {@code thor.alerts.enabled} property.
 * <p>
 * The alert checks are performed at an interval defined by {@code thor.metrics.collection.interval}.
 */
@AutoConfiguration
@EnableConfigurationProperties(ThorProperties.class)
@EnableScheduling // Enables Spring's scheduled task execution capabilities
@ConditionalOnProperty(prefix = "thor.alerts", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ThorAlertsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ThorAlertsAutoConfiguration.class);

    private final MeterRegistry meterRegistry;
    private final ThorProperties thorProperties;
    private final AlertService alertService; // Injected by Spring

    /**
     * Constructs the auto-configuration for Thor alerts.
     *
     * @param meterRegistry  The {@link MeterRegistry} to fetch metrics from.
     * @param thorProperties The {@link ThorProperties} containing alert configurations.
     * @param alertService   The {@link AlertService} to perform alert checks.
     */
    public ThorAlertsAutoConfiguration(MeterRegistry meterRegistry, ThorProperties thorProperties, AlertService alertService) {
        this.meterRegistry = meterRegistry;
        this.thorProperties = thorProperties;
        this.alertService = alertService;
    }

    /**
     * Defines the default {@link AlertService} implementation.
     *
     * @param properties The {@link ThorProperties} to be used by the alert service.
     * @return An instance of {@link DefaultAlertService}.
     */
    @Bean
    public AlertService defaultAlertService(ThorProperties properties) {
        return new DefaultAlertService(properties);
    }

    /**
     * Scheduled task to periodically check for alerts.
     * The rate of this task is configured by {@code thor.metrics.collection.interval},
     * defaulting to "60s" if not specified.
     * This method invokes {@link AlertService#checkForAlerts(MeterRegistry)}.
     */
    @Scheduled(fixedRateString = "#{thorProperties.metrics.collection.interval ?: '60s'}")
    public void scheduledAlertCheck() {
        String intervalProperty = this.thorProperties.getMetrics().getCollection().getInterval();
        if (!StringUtils.hasText(intervalProperty) || !intervalProperty.matches("\\d+[smhd]")) {
            // This warning is for user feedback; Spring's SpEL handles default for @Scheduled.
            // If the SpEL expression itself (e.g., property key) is wrong, startup might fail.
            log.warn("Invalid or empty 'thor.metrics.collection.interval' (value: '{}') for alert scheduling. " +
                     "The effective interval is determined by SpEL evaluation of '#{thorProperties.metrics.collection.interval ?: '60s'}'.",
                     intervalProperty);
        }
        log.debug("Executing scheduled alert check. Effective interval from SpEL: '{}'",
                  thorProperties.getMetrics().getCollection().getInterval() != null ?
                  thorProperties.getMetrics().getCollection().getInterval() : "60s (defaulted)");
        this.alertService.checkForAlerts(this.meterRegistry);
    }
}
