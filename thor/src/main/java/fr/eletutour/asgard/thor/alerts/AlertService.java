package fr.eletutour.asgard.thor.alerts;

import io.micrometer.core.instrument.MeterRegistry;

public interface AlertService {
    void checkForAlerts(MeterRegistry meterRegistry);
}
