package fr.eletutour.asgard.thor.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;
import java.util.Arrays;

/**
 * Configuration properties for the Thor module, prefixed with "thor".
 * Allows enabling/disabling features, setting thresholds, and configuring integrations.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "thor")
public class ThorProperties {

    /**
     * Globally enables or disables the Thor module.
     * If false, no metrics will be collected, and no alerts will be processed by Thor.
     */
    @NotNull
    private boolean enabled = true;

    /**
     * Configuration for metrics collection.
     */
    @NotNull
    private MetricsProperties metrics = new MetricsProperties();

    /**
     * Configuration for alerts.
     */
    @NotNull
    private AlertsProperties alerts = new AlertsProperties();

    /**
     * Configuration for integrations with external systems like Prometheus or Grafana.
     */
    @NotNull
    private IntegrationProperties integration = new IntegrationProperties();

    /**
     * Properties related to metrics collection.
     */
    @Data
    @Validated
    public static class MetricsProperties {
        /**
         * Enables or disables all metric collection features of Thor.
         * This acts as a master switch for metrics. If false, no Thor-managed MeterBinders will be registered.
         */
        @NotNull
        private boolean enabled = true;

        /**
         * Properties related to the collection parameters for various metrics.
         */
        @NotNull
        private CollectionProperties collection = new CollectionProperties();

        /**
         * Defines parameters for how and which metrics are collected.
         */
        @Data
        @Validated
        public static class CollectionProperties {
            /**
             * Interval at which certain metrics are collected.
             * Also used as the default check interval for alerts.
             * Accepts Spring Boot's Duration string format (e.g., "30s", "1m", "500ms").
             */
            @NotEmpty
            private String interval = "30s";

            /**
             * Enable or disable collection of HTTP server request metrics (e.g., latency, count).
             * Relies on Spring Boot Actuator's underlying mechanisms for web metrics.
             */
            @NotNull
            private boolean endpoints = true;

            /**
             * Enable or disable collection of system resource metrics.
             * This includes CPU usage (system.cpu.usage) and JVM memory usage (jvm.memory.used, jvm.memory.max).
             */
            @NotNull
            private boolean resources = true;

            /**
             * Enable or disable collection of database connection pool metrics.
             * Requires a DataSource bean to be present in the application context.
             */
            @NotNull
            private boolean database = true;

            /**
             * Enable or disable collection of detailed JVM metrics.
             * This includes garbage collection (jvm.gc.*), thread utilization (jvm.threads.*),
             * and class loader metrics (jvm.classes.*).
             */
            @NotNull
            private boolean jvm = true;
        }
    }

    /**
     * Properties related to alerting.
     */
    @Data
    @Validated
    public static class AlertsProperties {
        /**
         * Enables or disables the alerting feature.
         * If false, Thor will not check for or log any alerts.
         */
        @NotNull
        private boolean enabled = true;

        /**
         * Specifies the channels through which alerts should be sent.
         * Currently, this is a placeholder; alerts are logged as warnings.
         * Example: ["email", "slack", "custom_webhook"]
         */
        @NotEmpty
        private List<String> channels = Arrays.asList("email", "slack");

        /**
         * Defines the thresholds for triggering alerts.
         */
        @NotNull
        private ThresholdsProperties thresholds = new ThresholdsProperties();

        /**
         * Specifies metric thresholds that, if breached, will trigger an alert.
         */
        @Data
        @Validated
        public static class ThresholdsProperties {
            /**
             * Maximum acceptable average response time for HTTP requests.
             * Accepts a duration string (e.g., "1000ms", "1.5s").
             */
            @NotEmpty
            private String responseTime = "1000ms";

            /**
             * Maximum acceptable CPU usage as a percentage (e.g., "80%", "0.75").
             * If system CPU usage exceeds this value, an alert is triggered.
             */
            @Min(0)
            @Max(100)
            private int cpuUsage = 80;

            /**
             * Maximum acceptable heap memory usage (used vs. max) as a percentage (e.g., "85%", "0.8").
             * If JVM heap memory usage exceeds this percentage, an alert is triggered.
             */
            @Min(0)
            @Max(100)
            private int memoryUsage = 85;

            public Duration getResponseTimeDuration() {
                return Duration.parse("PT" + responseTime.replace("ms", "S"));
            }

            public double getCpuUsagePercentage() {
                return cpuUsage / 100.0;
            }

            public double getMemoryUsagePercentage() {
                return memoryUsage / 100.0;
            }
        }
    }

    /**
     * Properties related to integrations with external monitoring and visualization systems.
     */
    @Data
    @Validated
    public static class IntegrationProperties {
        /**
         * Configuration for Prometheus integration.
         */
        @NotNull
        private PrometheusProperties prometheus = new PrometheusProperties();

        /**
         * Configuration for Grafana integration.
         */
        @NotNull
        private GrafanaProperties grafana = new GrafanaProperties();

        /**
         * Properties for Prometheus integration.
         */
        @Data
        @Validated
        public static class PrometheusProperties {
            /**
             * Enables or disables integration with Prometheus.
             * If true, Thor configures the Spring Boot Actuator Prometheus endpoint (/actuator/prometheus)
             * to be enabled and exposed for scraping. If false, Thor ensures this endpoint is disabled.
             */
            @NotNull
            private boolean enabled = true;
        }

        /**
         * Properties for Grafana integration.
         */
        @Data
        @Validated
        public static class GrafanaProperties {
            /**
             * Placeholder for enabling/disabling Grafana integration features.
             * Specific Grafana integration features (e.g., automatic dashboard provisioning)
             * are not yet implemented in this version of Thor.
             */
            @NotNull
            private boolean enabled = true;
        }
    }
}
