package fr.eletutour.asgard.thor.autoconfigure;

import fr.eletutour.asgard.thor.alerts.AlertService;
import fr.eletutour.asgard.thor.config.ThorProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class ThorAlertsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ThorAlertsAutoConfiguration.class))
            .withUserConfiguration(ThorProperties.class) // Make ThorProperties available
            .withBean(MeterRegistry.class, SimpleMeterRegistry::new); // Provide a MeterRegistry

    @Test
    void alertServiceBeanIsPresentAndSchedulingIsEnabledByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ThorAlertsAutoConfiguration.class);
            assertThat(context).hasSingleBean(AlertService.class);
            // Also check if @EnableScheduling seems active by checking for related Spring beans,
            // e.g., ScheduledAnnotationBeanPostProcessor. This is a bit of an internal check.
            assertThat(context).hasBean("org.springframework.scheduling.annotation.internalScheduledAnnotationProcessor");
        });
    }

    @Test
    void alertServiceBeanIsAbsentWhenAlertsGloballyDisabled() {
        contextRunner
                .withPropertyValues("thor.alerts.enabled=false")
                .run(context -> {
                    // The auto-configuration itself should not be present
                    assertThat(context).doesNotHaveBean(ThorAlertsAutoConfiguration.class);
                    assertThat(context).doesNotHaveBean(AlertService.class);
                    // Scheduling related beans might still be present if another @Configuration enables it,
                    // but ThorAlertsAutoConfiguration won't be contributing to it.
                    // If ThorAlertsAutoConfiguration is the only one with @EnableScheduling,
                    // then this bean might also be absent.
                    // For this test, we primarily care that Thor's alert beans are gone.
                });
    }

    // Test for scheduled method invocation is more complex and would typically be an integration test
    // or require AOP testing utilities to verify method calls.
    // For unit testing the configuration, checking bean presence is the main goal.
}
