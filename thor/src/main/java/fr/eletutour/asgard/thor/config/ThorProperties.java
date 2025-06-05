package fr.eletutour.asgard.thor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Arrays;

@Data
@ConfigurationProperties(prefix = "thor")
public class ThorProperties {

    private boolean enabled = true;
    private MetricsProperties metrics = new MetricsProperties();
    private AlertsProperties alerts = new AlertsProperties();
    private IntegrationProperties integration = new IntegrationProperties();

    @Data
    public static class MetricsProperties {
        private boolean enabled = true;
        private CollectionProperties collection = new CollectionProperties();

        @Data
        public static class CollectionProperties {
            private String interval = "30s";
            private boolean endpoints = true;
            private boolean resources = true;
            private boolean database = true;
            private boolean jvm = true;
        }
    }

    @Data
    public static class AlertsProperties {
        private boolean enabled = true;
        private List<String> channels = Arrays.asList("email", "slack");
        private ThresholdsProperties thresholds = new ThresholdsProperties();

        @Data
        public static class ThresholdsProperties {
            private String responseTime = "1000ms";
            private String cpuUsage = "80%";
            private String memoryUsage = "85%";
        }
    }

    @Data
    public static class IntegrationProperties {
        private PrometheusProperties prometheus = new PrometheusProperties();
        private GrafanaProperties grafana = new GrafanaProperties();

        @Data
        public static class PrometheusProperties {
            private boolean enabled = true;
        }

        @Data
        public static class GrafanaProperties {
            private boolean enabled = true;
        }
    }
}
