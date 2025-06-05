package fr.eletutour.asgard.thor.autoconfigure;

import fr.eletutour.asgard.thor.config.ThorProperties;
import org.springframework.boot.actuate.metrics.jdbc.DataSourcePoolMetrics;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * Auto-configuration for Thor Metrics.
 * <p>
 * This configuration class is responsible for setting up and registering various
 * {@link MeterBinder} instances based on the application's configuration properties
 * defined in {@link ThorProperties}. It allows enabling or disabling different
 * categories of metrics such as system resources, JVM performance, and database connection pools.
 * <p>
 * Endpoint metrics (e.g., HTTP request times and counts) are primarily provided by Spring Boot Actuator's
 * own auto-configuration (e.g., WebMvcMetricsAutoConfiguration or WebFluxMetricsAutoConfiguration).
 * Thor's {@code thor.metrics.collection.endpoints} property serves as a global switch that can influence
 * whether these Actuator-provided metrics are effectively collected, though Actuator's own properties
 * (like {@code management.metrics.web.server.request.autotime.enabled}) provide finer control.
 * Thor respects the master switch {@code thor.metrics.enabled}.
 */
@AutoConfiguration
@EnableConfigurationProperties(ThorProperties.class)
@ConditionalOnProperty(prefix = "thor.metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ThorMetricsAutoConfiguration {

    // Note: Endpoint metrics are auto-configured by Spring Boot Actuator if
    // thor.metrics.collection.endpoints is true (handled by its own @ConditionalOnProperty)
    // and spring-boot-starter-actuator is present. This class doesn't need to explicitly
    // configure them but respects the thor.metrics.enabled master switch.

    /**
     * Provides CPU metrics (e.g., system.cpu.usage).
     * This bean is conditional on the property {@code thor.metrics.collection.resources=true}.
     *
     * @return A {@link MeterBinder} for CPU metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "resources", havingValue = "true", matchIfMissing = true)
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    /**
     * Provides JVM memory metrics (e.g., jvm.memory.used, jvm.memory.max, jvm.memory.committed).
     * This bean is conditional on the property {@code thor.metrics.collection.resources=true}.
     *
     * @return A {@link MeterBinder} for JVM memory metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "resources", havingValue = "true", matchIfMissing = true)
    public JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }

    /**
     * Provides JVM garbage collection metrics (e.g., jvm.gc.pause, jvm.gc.memory.allocated).
     * This bean is conditional on the property {@code thor.metrics.collection.jvm=true}.
     *
     * @return A {@link MeterBinder} for JVM GC metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "jvm", havingValue = "true", matchIfMissing = true)
    public JvmGcMetrics jvmGcMetrics() {
        return new JvmGcMetrics();
    }

    /**
     * Provides JVM thread metrics (e.g., jvm.threads.live, jvm.threads.daemon).
     * This bean is conditional on the property {@code thor.metrics.collection.jvm=true}.
     *
     * @return A {@link MeterBinder} for JVM thread metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "jvm", havingValue = "true", matchIfMissing = true)
    public JvmThreadMetrics jvmThreadMetrics() {
        return new JvmThreadMetrics();
    }

    /**
     * Provides JVM class loader metrics (e.g., jvm.classes.loaded, jvm.classes.unloaded).
     * This bean is conditional on the property {@code thor.metrics.collection.jvm=true}.
     *
     * @return A {@link MeterBinder} for JVM class loader metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "jvm", havingValue = "true", matchIfMissing = true)
    public ClassLoaderMetrics classLoaderMetrics() {
        return new ClassLoaderMetrics();
    }

    /**
     * Provides database connection pool metrics for an available {@link DataSource}.
     * This bean is conditional on the presence of a {@link DataSource} bean in the context
     * and the property {@code thor.metrics.collection.database=true}.
     *
     * @param dataSource The auto-configured {@link DataSource} instance.
     * @return A {@link MeterBinder} for database connection pool metrics.
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "database", havingValue = "true", matchIfMissing = true)
    public MeterBinder dataSourcePoolMetrics(DataSource dataSource) {
        // Using Collections.emptyList() for tags means the metrics will be named based on the pool type by default.
        return new DataSourcePoolMetrics(
                dataSource,
                Collections.emptyList(),
                "datasource",
                Collections.emptyList()
        );
    }
}
