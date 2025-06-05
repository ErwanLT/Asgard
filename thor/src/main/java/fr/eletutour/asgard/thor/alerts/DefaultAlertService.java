package fr.eletutour.asgard.thor.alerts;

import fr.eletutour.asgard.thor.config.ThorProperties;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DefaultAlertService implements AlertService {

    private static final Logger log = LoggerFactory.getLogger(DefaultAlertService.class);
    private final ThorProperties thorProperties;

    public DefaultAlertService(ThorProperties thorProperties) {
        this.thorProperties = thorProperties;
    }

    @Override
    public void checkForAlerts(MeterRegistry meterRegistry) {
        if (!thorProperties.getAlerts().isEnabled()) {
            return;
        }

        log.debug("Checking for alerts...");

        checkResponseTimeThreshold(meterRegistry);
        checkCpuUsageThreshold(meterRegistry);
        checkMemoryUsageThreshold(meterRegistry);
    }

    private void checkResponseTimeThreshold(MeterRegistry meterRegistry) {
        String thresholdStr = thorProperties.getAlerts().getThresholds().getResponseTime();
        long thresholdMillis = parseMillisFromString(thresholdStr);
        if (thresholdMillis <= 0) {
            log.warn("Invalid or disabled response time threshold: {}", thresholdStr);
            return;
        }

        // Attempt to find a common timer for HTTP server requests
        // This name can vary based on web server (Tomcat, Jetty, Netty) and configuration
        // Common names: "http.server.requests"
        Timer requestTimer = meterRegistry.find("http.server.requests").timer();
        if (requestTimer != null) {
            double maxResponseTimeMillis = requestTimer.max(TimeUnit.MILLISECONDS);
            if (maxResponseTimeMillis > thresholdMillis) {
                log.warn("Alert: Maximum response time ({:.2f}ms) exceeded threshold ({}ms). Timer: {}",
                        maxResponseTimeMillis, thresholdMillis, requestTimer.getId().getName());
            }
            // Also checking average, though max is often more critical for alerts
            double meanResponseTimeMillis = requestTimer.mean(TimeUnit.MILLISECONDS);
            if (meanResponseTimeMillis > thresholdMillis) {
                 log.warn("Alert: Average response time ({:.2f}ms) exceeded threshold ({}ms). Timer: {}",
                        meanResponseTimeMillis, thresholdMillis, requestTimer.getId().getName());
            }
        } else {
            log.debug("No 'http.server.requests' timer found to check response time threshold.");
        }
    }

    private void checkCpuUsageThreshold(MeterRegistry meterRegistry) {
        String thresholdStr = thorProperties.getAlerts().getThresholds().getCpuUsage();
        double thresholdPercent = parsePercentFromString(thresholdStr);
        if (thresholdPercent <= 0) {
            log.warn("Invalid or disabled CPU usage threshold: {}", thresholdStr);
            return;
        }

        Gauge cpuUsageGauge = meterRegistry.find("system.cpu.usage").gauge();
        if (cpuUsageGauge != null) {
            double currentCpuUsage = cpuUsageGauge.value();
            if (currentCpuUsage > thresholdPercent) {
                log.warn("Alert: CPU usage ({:.2%}) exceeded threshold ({:.2%})", currentCpuUsage, thresholdPercent);
            }
        } else {
            log.debug("No 'system.cpu.usage' gauge found to check CPU usage threshold.");
        }
    }

    private void checkMemoryUsageThreshold(MeterRegistry meterRegistry) {
        String thresholdStr = thorProperties.getAlerts().getThresholds().getMemoryUsage();
        double thresholdPercent = parsePercentFromString(thresholdStr);
        if (thresholdPercent <= 0) {
            log.warn("Invalid or disabled memory usage threshold: {}", thresholdStr);
            return;
        }

        Gauge usedMemoryGauge = meterRegistry.find("jvm.memory.used").tag("area", "heap").gauge();
        Gauge maxMemoryGauge = meterRegistry.find("jvm.memory.max").tag("area", "heap").gauge();

        if (usedMemoryGauge != null && maxMemoryGauge != null) {
            double usedMemory = usedMemoryGauge.value();
            double maxMemory = maxMemoryGauge.value();
            if (maxMemory > 0) {
                double currentMemoryUsagePercent = usedMemory / maxMemory;
                if (currentMemoryUsagePercent > thresholdPercent) {
                    log.warn("Alert: Memory usage ({:.2%}) exceeded threshold ({:.2%}). Used: {}, Max: {}",
                            currentMemoryUsagePercent, thresholdPercent, (long)usedMemory, (long)maxMemory);
                }
            }
        } else {
            log.debug("No 'jvm.memory.used' or 'jvm.memory.max' (heap) gauges found to check memory usage threshold.");
        }
    }

    private long parseMillisFromString(String value) {
        if (value == null || !value.toLowerCase().endsWith("ms")) {
            return -1;
        }
        try {
            return Long.parseLong(value.substring(0, value.length() - 2));
        } catch (NumberFormatException e) {
            log.error("Failed to parse millisecond value from: {}", value, e);
            return -1;
        }
    }

    private double parsePercentFromString(String value) {
        if (value == null || !value.endsWith("%")) {
            return -1;
        }
        try {
            return Double.parseDouble(value.substring(0, value.length() - 1)) / 100.0;
        } catch (NumberFormatException e) {
            log.error("Failed to parse percentage value from: {}", value, e);
            return -1;
        }
    }
}
