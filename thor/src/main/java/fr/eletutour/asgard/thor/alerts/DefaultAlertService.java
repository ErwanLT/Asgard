package fr.eletutour.asgard.thor.alerts;

import fr.eletutour.asgard.thor.config.ThorProperties;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link AlertService}.
 * This service checks metrics for response time, CPU usage, and memory usage
 * against thresholds defined in {@link ThorProperties}.
 * If a threshold is breached, a warning message is logged via SLF4J.
 * Alert notifications to external channels (e.g., email, Slack) are planned
 * but not implemented in this version; currently, alerts are only logged.
 */
@Service
public class DefaultAlertService implements AlertService {

    private static final Logger log = LoggerFactory.getLogger(DefaultAlertService.class);
    private final ThorProperties thorProperties;

    /**
     * Constructs a new DefaultAlertService.
     *
     * @param thorProperties The configuration properties for Thor, containing alert thresholds.
     */
    public DefaultAlertService(ThorProperties thorProperties) {
        this.thorProperties = thorProperties;
    }

    /**
     * Performs the alert check process.
     * It first checks if alerting is globally enabled via {@code thor.alerts.enabled}.
     * If enabled, it proceeds to check specific metrics:
     * <ul>
     *     <li>Response Time: Compares max and mean of {@code http.server.requests} timer against {@code thor.alerts.thresholds.responseTime}.</li>
     *     <li>CPU Usage: Compares {@code system.cpu.usage} gauge against {@code thor.alerts.thresholds.cpuUsage}.</li>
     *     <li>Memory Usage: Compares heap memory usage (derived from {@code jvm.memory.used} and {@code jvm.memory.max})
     *         against {@code thor.alerts.thresholds.memoryUsage}.</li>
     * </ul>
     * Breached thresholds result in logged warnings.
     *
     * @param meterRegistry The {@link MeterRegistry} to query for metric values.
     */
    @Override
    public void checkForAlerts(MeterRegistry meterRegistry) {
        if (!thorProperties.getAlerts().isEnabled()) {
            return;
        }

        log.debug("Checking for alerts based on configured thresholds...");

        checkResponseTimeThreshold(meterRegistry);
        checkCpuUsageThreshold(meterRegistry);
        checkMemoryUsageThreshold(meterRegistry);
    }

    /**
     * Checks the response time metric against its configured threshold.
     * It looks for a timer named "http.server.requests" and compares its maximum and mean values
     * (converted to milliseconds) against the threshold specified in {@code thor.alerts.thresholds.responseTime}.
     *
     * @param meterRegistry The meter registry to query.
     */
    private void checkResponseTimeThreshold(MeterRegistry meterRegistry) {
        String thresholdStr = thorProperties.getAlerts().getThresholds().getResponseTime();
        long thresholdMillis = parseMillisFromString(thresholdStr);
        if (thresholdMillis <= 0) {
            log.debug("Response time threshold is invalid or disabled: {}", thresholdStr);
            return;
        }

        Timer requestTimer = meterRegistry.find("http.server.requests").timer();
        if (requestTimer != null) {
            double maxResponseTimeMillis = requestTimer.max(TimeUnit.MILLISECONDS);
            if (maxResponseTimeMillis > thresholdMillis) {
                log.warn("Alert: Maximum response time ({:.2f}ms) exceeded threshold ({}ms). Timer: {}",
                        maxResponseTimeMillis, thresholdMillis, requestTimer.getId().getName());
            }
            double meanResponseTimeMillis = requestTimer.mean(TimeUnit.MILLISECONDS);
            if (meanResponseTimeMillis > thresholdMillis) {
                 log.warn("Alert: Average response time ({:.2f}ms) exceeded threshold ({}ms). Timer: {}",
                        meanResponseTimeMillis, thresholdMillis, requestTimer.getId().getName());
            }
        } else {
            log.debug("No 'http.server.requests' timer found. Cannot check response time threshold.");
        }
    }

    /**
     * Checks the CPU usage metric against its configured threshold.
     * It looks for a gauge named "system.cpu.usage" and compares its value
     * against the percentage threshold specified in {@code thor.alerts.thresholds.cpuUsage}.
     *
     * @param meterRegistry The meter registry to query.
     */
    private void checkCpuUsageThreshold(MeterRegistry meterRegistry) {
        String thresholdStr = String.valueOf(thorProperties.getAlerts().getThresholds().getCpuUsage());
        double thresholdPercent = parsePercentFromString(thresholdStr);
        if (thresholdPercent <= 0) {
            log.debug("CPU usage threshold is invalid or disabled: {}", thresholdStr);
            return;
        }

        Gauge cpuUsageGauge = meterRegistry.find("system.cpu.usage").gauge();
        if (cpuUsageGauge != null) {
            double currentCpuUsage = cpuUsageGauge.value(); // Value is typically 0.0 to 1.0
            if (currentCpuUsage > thresholdPercent) {
                log.warn("Alert: CPU usage ({:.2%}) exceeded threshold ({:.2%})", currentCpuUsage, thresholdPercent);
            }
        } else {
            log.debug("No 'system.cpu.usage' gauge found. Cannot check CPU usage threshold.");
        }
    }

    /**
     * Checks the JVM heap memory usage metric against its configured threshold.
     * It looks for gauges "jvm.memory.used" and "jvm.memory.max" (with tag area="heap"),
     * calculates the usage percentage, and compares it against {@code thor.alerts.thresholds.memoryUsage}.
     *
     * @param meterRegistry The meter registry to query.
     */
    private void checkMemoryUsageThreshold(MeterRegistry meterRegistry) {
        String thresholdStr = String.valueOf(thorProperties.getAlerts().getThresholds().getMemoryUsage());
        double thresholdPercent = parsePercentFromString(thresholdStr);
        if (thresholdPercent <= 0) {
            log.debug("Memory usage threshold is invalid or disabled: {}", thresholdStr);
            return;
        }

        Gauge usedMemoryGauge = meterRegistry.find("jvm.memory.used").tag("area", "heap").gauge();
        Gauge maxMemoryGauge = meterRegistry.find("jvm.memory.max").tag("area", "heap").gauge();

        if (usedMemoryGauge != null && maxMemoryGauge != null) {
            double usedMemory = usedMemoryGauge.value();
            double maxMemory = maxMemoryGauge.value();
            if (maxMemory > 0) { // Avoid division by zero if max memory is not available or zero
                double currentMemoryUsagePercent = usedMemory / maxMemory;
                if (currentMemoryUsagePercent > thresholdPercent) {
                    log.warn("Alert: Heap memory usage ({:.2%}) exceeded threshold ({:.2%}). Used: {} bytes, Max: {} bytes",
                            currentMemoryUsagePercent, thresholdPercent, (long)usedMemory, (long)maxMemory);
                }
            } else {
                log.debug("Max heap memory (jvm.memory.max tag area='heap') is zero or unavailable. Cannot calculate usage percentage.");
            }
        } else {
            log.debug("No 'jvm.memory.used' or 'jvm.memory.max' (heap area) gauges found. Cannot check memory usage threshold.");
        }
    }

    /**
     * Parses a string representing milliseconds (e.g., "1000ms") into a long value.
     *
     * @param value The string to parse. Must end with "ms".
     * @return The parsed millisecond value, or -1 if parsing fails or input is invalid.
     */
    private long parseMillisFromString(String value) {
        if (value == null || !value.toLowerCase().endsWith("ms")) {
            log.trace("Invalid format for millisecond string: {}. Must end with 'ms'.", value);
            return -1;
        }
        try {
            return Long.parseLong(value.substring(0, value.length() - 2));
        } catch (NumberFormatException e) {
            log.error("Failed to parse millisecond value from string: {}", value, e);
            return -1;
        }
    }

    /**
     * Parses a string representing a percentage (e.g., "80%") into a double value (0.0 to 1.0).
     *
     * @param value The string to parse. Must end with "%".
     * @return The parsed percentage value (e.g., 0.8 for "80%"), or -1 if parsing fails or input is invalid.
     */
    private double parsePercentFromString(String value) {
        if (value == null || !value.endsWith("%")) {
            log.trace("Invalid format for percentage string: {}. Must end with '%'.", value);
            return -1;
        }
        try {
            return Double.parseDouble(value.substring(0, value.length() - 1)) / 100.0;
        } catch (NumberFormatException e) {
            log.error("Failed to parse percentage value from string: {}", value, e);
            return -1;
        }
    }
}
