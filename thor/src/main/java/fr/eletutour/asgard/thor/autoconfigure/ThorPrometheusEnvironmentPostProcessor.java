package fr.eletutour.asgard.thor.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ThorPrometheusEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    // Run after Spring Boot's default property loading but before the application context is refreshed.
    public static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE - 10; // Arbitrary order

    private static final String THOR_PROMETHEUS_ENABLED_PROPERTY = "thor.integration.prometheus.enabled";
    private static final String SPRING_PROMETHEUS_ENABLED_PROPERTY = "management.endpoint.prometheus.enabled";
    private static final String SPRING_EXPOSURE_INCLUDE_PROPERTY = "management.endpoints.web.exposure.include";
    private static final String SPRING_EXPOSURE_EXCLUDE_PROPERTY = "management.endpoints.web.exposure.exclude";
    private static final String PROMETHEUS_ENDPOINT_ID = "prometheus";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Default to true if the property is not explicitly set
        boolean thorPrometheusEnabled = environment.getProperty(THOR_PROMETHEUS_ENABLED_PROPERTY, Boolean.class, true);

        Map<String, Object> thorManagedProperties = new HashMap<>();

        if (thorPrometheusEnabled) {
            thorManagedProperties.put(SPRING_PROMETHEUS_ENABLED_PROPERTY, true);
            // Add "prometheus" to includes
            addToExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_INCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
            // Ensure "prometheus" is not in excludes (though less critical if already in includes)
            removeFromExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_EXCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
        } else {
            thorManagedProperties.put(SPRING_PROMETHEUS_ENABLED_PROPERTY, false);
            // Add "prometheus" to excludes
            addToExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_EXCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
            // Ensure "prometheus" is not in includes
            removeFromExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_INCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
        }

        if (!thorManagedProperties.isEmpty()) {
            PropertySource<?> existingPs = environment.getPropertySources().get("thorDefaultProperties");
            if (existingPs != null && existingPs.getSource() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> existingMap = (Map<String, Object>) existingPs.getSource();
                existingMap.putAll(thorManagedProperties);
            } else {
                 environment.getPropertySources().addLast(new MapPropertySource("thorDefaultProperties", thorManagedProperties));
            }
        }
    }

    private void addToExposureList(ConfigurableEnvironment environment, Map<String, Object> targetMap, String exposurePropertyKey, String endpointId) {
        String currentExposure = environment.getProperty(exposurePropertyKey, "");
        Set<String> exposureSet = StringUtils.hasText(currentExposure) ?
                new LinkedHashSet<>(Arrays.asList(currentExposure.split(","))) :
                new LinkedHashSet<>();
        if (exposureSet.add(endpointId)) { // add returns true if the element was added (not already present)
            targetMap.put(exposurePropertyKey, String.join(",", exposureSet));
        } else if (currentExposure.isEmpty() && endpointId.equals(PROMETHEUS_ENDPOINT_ID) && exposurePropertyKey.equals(SPRING_EXPOSURE_INCLUDE_PROPERTY)){
            // If include is empty, and we are adding prometheus, explicitly set it.
             targetMap.put(exposurePropertyKey, endpointId);
        }

    }

    private void removeFromExposureList(ConfigurableEnvironment environment, Map<String, Object> targetMap, String exposurePropertyKey, String endpointId) {
        String currentExposure = environment.getProperty(exposurePropertyKey, "");
        if (StringUtils.hasText(currentExposure)) {
            Set<String> exposureSet = new LinkedHashSet<>(Arrays.asList(currentExposure.split(",")));
            if (exposureSet.remove(endpointId)) { // remove returns true if the element was present and removed
                targetMap.put(exposurePropertyKey, String.join(",", exposureSet));
            }
        }
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
