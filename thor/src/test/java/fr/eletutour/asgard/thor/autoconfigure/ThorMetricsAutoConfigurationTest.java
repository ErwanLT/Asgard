package fr.eletutour.asgard.thor.autoconfigure;

import fr.eletutour.asgard.thor.config.ThorProperties;
import io.micrometer.core.instrument.binder.db.DataSourcePoolMetrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.mock.mockito.MockBean; // For mock DataSource
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ThorMetricsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ThorMetricsAutoConfiguration.class, ThorAlertsAutoConfiguration.class))
            .withUserConfiguration(ThorProperties.class); // Make ThorProperties available for injection

    @Test
    void defaultMetricBeansArePresentWhenAllEnabled() {
        contextRunner
                .withBean(DataSource.class, () -> mock(DataSource.class)) // Provide DataSource mock
                .run(context -> {
                    assertThat(context).hasSingleBean(ThorMetricsAutoConfiguration.class);
                    // Metrics properties default to true
                    assertThat(context).hasSingleBean(ProcessorMetrics.class); // resources
                    assertThat(context).hasSingleBean(JvmMemoryMetrics.class); // resources
                    assertThat(context).hasSingleBean(JvmGcMetrics.class);     // jvm
                    assertThat(context).hasSingleBean(JvmThreadMetrics.class); // jvm
                    assertThat(context).hasSingleBean(ClassLoaderMetrics.class); // jvm
                    assertThat(context).hasSingleBean(DataSourcePoolMetrics.class); // database (needs DataSource)

                    // Endpoint metrics are trickier as they are configured by Spring Boot Actuator itself.
                    // ThorMetricsAutoConfiguration doesn't create endpoint metric beans directly.
                    // We rely on thor.metrics.collection.endpoints=true enabling Actuator's WebMvcMetricsAutoConfiguration.
                    // Testing that requires a full web context or specific checks for Actuator's beans.
                    // For now, we confirm our own MeterBinders.
                });
    }

    @Test
    void metricsBeansAreAbsentWhenMetricsGloballyDisabled() {
        contextRunner
                .withPropertyValues("thor.metrics.enabled=false")
                .withBean(DataSource.class, () -> mock(DataSource.class))
                .run(context -> {
                    // The auto-configuration itself should not be present if master switch is off
                    assertThat(context).doesNotHaveBean(ThorMetricsAutoConfiguration.class);
                    assertThat(context).doesNotHaveBean(ProcessorMetrics.class);
                    assertThat(context).doesNotHaveBean(JvmMemoryMetrics.class);
                    assertThat(context).doesNotHaveBean(JvmGcMetrics.class);
                    assertThat(context).doesNotHaveBean(JvmThreadMetrics.class);
                    assertThat(context).doesNotHaveBean(ClassLoaderMetrics.class);
                    assertThat(context).doesNotHaveBean(DataSourcePoolMetrics.class);
                });
    }

    @Test
    void jvmMetricsBeansAreAbsentWhenJvmCollectionDisabled() {
        contextRunner
                .withPropertyValues("thor.metrics.collection.jvm=false")
                .withBean(DataSource.class, () -> mock(DataSource.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(ThorMetricsAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ProcessorMetrics.class); // Resources should still be active
                    assertThat(context).hasSingleBean(JvmMemoryMetrics.class); // Resources should still be active
                    assertThat(context).hasSingleBean(DataSourcePoolMetrics.class); // Database should still be active

                    assertThat(context).doesNotHaveBean(JvmGcMetrics.class);
                    assertThat(context).doesNotHaveBean(JvmThreadMetrics.class);
                    assertThat(context).doesNotHaveBean(ClassLoaderMetrics.class);
                });
    }

    @Test
    void resourceMetricsBeansAreAbsentWhenResourceCollectionDisabled() {
        contextRunner
                .withPropertyValues("thor.metrics.collection.resources=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(ThorMetricsAutoConfiguration.class);
                    assertThat(context).hasSingleBean(JvmGcMetrics.class); // JVM should still be active

                    assertThat(context).doesNotHaveBean(ProcessorMetrics.class);
                    // JvmMemoryMetrics is under 'resources', so it should be absent
                    assertThat(context).doesNotHaveBean(JvmMemoryMetrics.class);
                });
    }

    @Test
    void databaseMetricsBeansAreAbsentWhenDatabaseCollectionDisabled() {
        contextRunner
                .withPropertyValues("thor.metrics.collection.database=false")
                .withBean(DataSource.class, () -> mock(DataSource.class)) // Still provide DataSource
                .run(context -> {
                    assertThat(context).hasSingleBean(ThorMetricsAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ProcessorMetrics.class); // Resources should still be active

                    assertThat(context).doesNotHaveBean(DataSourcePoolMetrics.class);
                });
    }

    @Test
    void databaseMetricsBeansAreAbsentWhenNoDataSourcePresent() {
        contextRunner
                // No DataSource bean provided
                .run(context -> {
                    assertThat(context).hasSingleBean(ThorMetricsAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ProcessorMetrics.class); // Resources should still be active

                    // DataSourcePoolMetrics bean is conditional on DataSource bean
                    assertThat(context).doesNotHaveBean(DataSourcePoolMetrics.class);
                });
    }
}
