package fr.eletutour.asgard.thor.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestConfig.class, ThorProperties.class})
@ActiveProfiles("test")
class ThorPropertiesTest {

    @Autowired
    private ThorProperties properties;

    @Test
    void shouldLoadDefaultProperties() {
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getMetrics().getCollection().getInterval()).isEqualTo("30s");
        assertThat(properties.getAlerts().isEnabled()).isTrue();
        assertThat(properties.getAlerts().getChannels()).containsExactly("email", "slack");
        assertThat(properties.getIntegration().getPrometheus().isEnabled()).isTrue();
    }

    @Test
    void shouldLoadCustomProperties() {
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getMetrics().getCollection().getInterval()).isEqualTo("60s");
        assertThat(properties.getAlerts().isEnabled()).isFalse();
        assertThat(properties.getAlerts().getChannels()).containsExactly("email", "sms");
        assertThat(properties.getIntegration().getPrometheus().isEnabled()).isFalse();
    }
}
