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
// Unused import: java.util.stream.Collectors;

/**
 * An {@link EnvironmentPostProcessor} that configures Spring Boot Actuator's Prometheus endpoint
 * based on Thor module's specific properties.
 * <p>
 * This post-processor reads {@code thor.integration.prometheus.enabled} and translates it into
 * the appropriate Spring Boot Actuator properties:
 * <ul>
 *     <li>{@code management.endpoint.prometheus.enabled}</li>
 *     <li>Manages the inclusion/exclusion of "prometheus" in {@code management.endpoints.web.exposure.include}
 *         and {@code management.endpoints.web.exposure.exclude}.</li>
 * </ul>
 * This allows Thor to centrally manage the availability of the Prometheus scraping endpoint
 * (typically {@code /actuator/prometheus}) via its own configuration namespace.
 * It runs with a defined order to apply these properties before the application context fully initializes
 * but after standard Spring Boot property loading.
 */
public class ThorPrometheusEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * The order for this post-processor. It's set to run relatively late,
     * after Spring Boot's own default property processing but before the main application context refresh,
     * to ensure it can override or set defaults for Actuator properties based on Thor's config.
     */
    public static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE - 10;

    private static final String THOR_PROMETHEUS_ENABLED_PROPERTY = "thor.integration.prometheus.enabled";
    private static final String SPRING_PROMETHEUS_ENABLED_PROPERTY = "management.endpoint.prometheus.enabled";
    private static final String SPRING_EXPOSURE_INCLUDE_PROPERTY = "management.endpoints.web.exposure.include";
    private static final String SPRING_EXPOSURE_EXCLUDE_PROPERTY = "management.endpoints.web.exposure.exclude";
    private static final String PROMETHEUS_ENDPOINT_ID = "prometheus";
    private static final String THOR_MANAGED_PROPERTY_SOURCE_NAME = "thorManagedActuatorProperties";


    /**
     * Post-processes the {@link ConfigurableEnvironment}.
     * This method applies Prometheus-related Actuator properties based on the value of
     * {@code thor.integration.prometheus.enabled}.
     *
     * @param environment The configurable environment.
     * @param application The Spring application.
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Default to true for thor.integration.prometheus.enabled if the property is not explicitly set,
        // aligning with ThorProperties default.
        boolean thorPrometheusEnabled = environment.getProperty(THOR_PROMETHEUS_ENABLED_PROPERTY, Boolean.class, true);

        Map<String, Object> thorManagedProperties = new HashMap<>();

        if (thorPrometheusEnabled) {
            thorManagedProperties.put(SPRING_PROMETHEUS_ENABLED_PROPERTY, true);
            addToExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_INCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
            removeFromExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_EXCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
        } else {
            // This is the critical part for disabling: it ensures the endpoint itself is not enabled.
            thorManagedProperties.put(SPRING_PROMETHEUS_ENABLED_PROPERTY, false);
            // Also, explicitly try to remove it from general exposure if it was there.
            addToExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_EXCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
            removeFromExposureList(environment, thorManagedProperties, SPRING_EXPOSURE_INCLUDE_PROPERTY, PROMETHEUS_ENDPOINT_ID);
        }

        if (!thorManagedProperties.isEmpty()) {
            // Add these properties with lower precedence so user-defined properties can still override them.
            // If the property source already exists (e.g. from a previous run, though unlikely for EPPs), update it.
            PropertySource<?> existingPs = environment.getPropertySources().get(THOR_MANAGED_PROPERTY_SOURCE_NAME);
            if (existingPs != null && existingPs.getSource() instanceof Map) {
                @SuppressWarnings("unchecked") // Source is checked to be a Map
                Map<String, Object> existingMap = (Map<String, Object>) existingPs.getSource();
                existingMap.putAll(thorManagedProperties);
            } else {
                // Add last to give it lower precedence than application.properties etc.
                 environment.getPropertySources().addLast(
                         new MapPropertySource(THOR_MANAGED_PROPERTY_SOURCE_NAME, thorManagedProperties));
            }
        }
    }

    /**
     * Helper method to add an endpoint ID to an exposure list property (include/exclude).
     * It reads the current list from the environment, adds the new ID if not present,
     * and prepares the updated list to be put into the target properties map.
     * Handles comma-separated string lists.
     *
     * @param environment The current environment to read existing property value.
     * @param targetMap The map where the potentially updated property value will be stored.
     * @param exposurePropertyKey The key of the exposure property (e.g., "management.endpoints.web.exposure.include").
     * @param endpointId The endpoint ID to add (e.g., "prometheus").
     */
    private void addToExposureList(ConfigurableEnvironment environment, Map<String, Object> targetMap, String exposurePropertyKey, String endpointId) {
        String currentExposure = environment.getProperty(exposurePropertyKey, "");
        Set<String> exposureSet = StringUtils.hasText(currentExposure) && !currentExposure.equals("*") ?
                new LinkedHashSet<>(Arrays.asList(currentExposure.split(","))) :
                new LinkedHashSet<>();

        if (currentExposure.equals("*")) { // If it's already "*", do nothing for includes. For excludes, this logic might need review if we want to exclude from "*".
            if (exposurePropertyKey.equals(SPRING_EXPOSURE_EXCLUDE_PROPERTY)) { // If we are adding to exclude list
                 if (exposureSet.add(endpointId)) { // if '*' was somehow in exclude (invalid) or it was empty
                    targetMap.put(exposurePropertyKey, String.join(",", exposureSet));
                }
            } // else if key is INCLUDE_PROPERTY and currentExposure is "*", don't touch it.
            return;
        }

        if (exposureSet.add(endpointId)) {
            targetMap.put(exposurePropertyKey, String.join(",", exposureSet));
        } else if (currentExposure.isEmpty() && exposurePropertyKey.equals(SPRING_EXPOSURE_INCLUDE_PROPERTY)) {
            // If include list was empty and we are adding an item (e.g. "prometheus"), set it.
             targetMap.put(exposurePropertyKey, endpointId);
        }
    }

    /**
     * Helper method to remove an endpoint ID from an exposure list property.
     * Reads the current list, removes the ID if present, and prepares the updated list.
     * Handles comma-separated string lists. If current list is "*", it's not modified.
     *
     * @param environment The current environment.
     * @param targetMap The map for updated properties.
     * @param exposurePropertyKey The exposure property key.
     * @param endpointId The endpoint ID to remove.
     */
    private void removeFromExposureList(ConfigurableEnvironment environment, Map<String, Object> targetMap, String exposurePropertyKey, String endpointId) {
        String currentExposure = environment.getProperty(exposurePropertyKey, "");
        if (StringUtils.hasText(currentExposure) && !currentExposure.equals("*")) {
            Set<String> exposureSet = new LinkedHashSet<>(Arrays.asList(currentExposure.split(",")));
            if (exposureSet.remove(endpointId)) {
                targetMap.put(exposurePropertyKey, String.join(",", exposureSet));
            }
        }
        // If currentExposure is "*", we don't try to remove a specific endpoint from it.
        // The main control is `management.endpoint.<id>.enabled=false`.
    }


    /**
     * Defines the order of this {@link EnvironmentPostProcessor}.
     * @return The order value.
     */
    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
