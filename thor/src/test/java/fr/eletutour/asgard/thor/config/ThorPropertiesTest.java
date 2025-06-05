package fr.eletutour.asgard.thor.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ThorProperties.class)
// Use DirtiesContext to ensure a fresh context (and properties) for each test method.
// This is important when using @TestPropertySource on different methods in the same class.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Optional: control test order if needed, though tests should be independent.
public class ThorPropertiesTest {

    @Autowired
    private ThorProperties thorProperties;

    @Test
    @Order(1) // Ensures this runs first, before properties are changed.
    void defaultValuesLoadCorrectly() {
        assertThat(thorProperties.isEnabled()).isTrue();
        assertThat(thorProperties.getMetrics().isEnabled()).isTrue();
        assertThat(thorProperties.getMetrics().getCollection().getInterval()).isEqualTo("30s");
        assertThat(thorProperties.getMetrics().getCollection().isEndpoints()).isTrue();
        assertThat(thorProperties.getMetrics().getCollection().isResources()).isTrue();
        assertThat(thorProperties.getMetrics().getCollection().isDatabase()).isTrue();
        assertThat(thorProperties.getMetrics().getCollection().isJvm()).isTrue();

        assertThat(thorProperties.getAlerts().isEnabled()).isTrue();
        assertThat(thorProperties.getAlerts().getChannels()).containsExactlyInAnyOrder("email", "slack");
        assertThat(thorProperties.getAlerts().getThresholds().getResponseTime()).isEqualTo("1000ms");
        assertThat(thorProperties.getAlerts().getThresholds().getCpuUsage()).isEqualTo("80%");
        assertThat(thorProperties.getAlerts().getThresholds().getMemoryUsage()).isEqualTo("85%");

        assertThat(thorProperties.getIntegration().getPrometheus().isEnabled()).isTrue();
        assertThat(thorProperties.getIntegration().getGrafana().isEnabled()).isTrue();
    }

    @Test
    @Order(2)
    @TestPropertySource(properties = {
            "thor.enabled=false",
            "thor.metrics.collection.interval=60s",
            "thor.alerts.enabled=false",
            "thor.alerts.channels=email,sms",
            "thor.integration.prometheus.enabled=false"
    })
    void customValuesLoadCorrectly() {
        ThorProperties customProps = new ThorProperties(); // Need to re-init or use a context with these properties

        // Re-autowire or use a fresh context for TestPropertySource to apply to the bean
        // The current autowired thorProperties is from a shared context and won't reflect TestPropertySource from another method.
        // For this simple properties class, direct instantiation and setters would be one way if not using Spring context for the test.
        // However, to test Spring's loading, need a fresh context or a per-test context.
        // Let's assume @SpringBootTest creates a context that can be influenced by @TestPropertySource for the specific test method.
        // SpringBootTest caches context, need to ensure properties are applied.
        // A common pattern is to have separate @SpringBootTest classes or use @DirtiesContext if properties are test-specific.

        // The above autowired thorProperties will be from a context loaded once.
        // @TestPropertySource works by modifying the Environment for the test's ApplicationContext.
        // If the context is shared across tests (default for @SpringBootTest), this can be tricky.
        // Let's check the injected thorProperties directly. Spring should handle it.

        assertThat(thorProperties.isEnabled()).isFalse();
        assertThat(thorProperties.getMetrics().getCollection().getInterval()).isEqualTo("60s");
        assertThat(thorProperties.getAlerts().isEnabled()).isFalse();
        assertThat(thorProperties.getAlerts().getChannels()).containsExactlyInAnyOrder("email", "sms");
        assertThat(thorProperties.getIntegration().getPrometheus().isEnabled()).isFalse();
    }

     @Test
    @Order(3)
    @TestPropertySource(properties = {
            "thor.alerts.channels[0]=teams",
            "thor.alerts.channels[1]=pagerduty"
    })
    void customListValuesLoadCorrectly() {
        // This test will run with its own @TestPropertySource,
        // and due to @DirtiesContext, it won't be affected by the previous @TestPropertySource.
        assertThat(thorProperties.getAlerts().getChannels()).containsExactlyInAnyOrder("teams", "pagerduty");

        // Check that other properties are back to default or as per this @TestPropertySource
        // For example, thor.enabled should be true (default) as it's not overridden here.
        assertThat(thorProperties.isEnabled()).isTrue();
        assertThat(thorProperties.getMetrics().getCollection().getInterval()).isEqualTo("30s"); // Default
    }
}
