package fr.eletutour.asgard.thor.alerts;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Interface for the alerting service.
 * Implementations of this interface are responsible for checking metric values
 * against configured thresholds and triggering alerts if breaches are detected.
 */
public interface AlertService {

    /**
     * Checks all relevant metrics from the given {@link MeterRegistry} against
     * configured thresholds and triggers alerts for any breaches.
     *
     * @param meterRegistry The {@link MeterRegistry} instance containing the metrics to check.
     */
    void checkForAlerts(MeterRegistry meterRegistry);
}
