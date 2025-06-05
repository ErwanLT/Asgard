package fr.eletutour.asgard.thor.autoconfigure;

import fr.eletutour.asgard.thor.config.ThorProperties;
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
import io.micrometer.core.instrument.binder.db.DataSourcePoolMetrics;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * Auto-configuration for Thor Metrics.
 * <p>
 * This configuration enables and configures metrics collection based on the properties
 * defined in {@link ThorProperties}.
 * <p>
 * Endpoint metrics (e.g., HTTP request times and counts) are provided by Spring Boot Actuator's
 * WebMvcMetricsAutoConfiguration or WebFluxMetricsAutoConfiguration. Their collection is
 * enabled/disabled globally by {@code thor.metrics.collection.endpoints}.
 * Further customization of Actuator's endpoint metrics can be done using Spring Boot's
 * {@code management.metrics.web.server.request.*} properties.
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
     * Provides CPU metrics.
     * Activated if {@code thor.metrics.collection.resources} is true.
     *
     * @return MeterBinder for CPU metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "resources", havingValue = "true", matchIfMissing = true)
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    /**
     * Provides JVM memory metrics.
     * Activated if {@code thor.metrics.collection.resources} is true.
     *
     * @return MeterBinder for JVM memory metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "resources", havingValue = "true", matchIfMissing = true)
    public JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }

    /**
     * Provides JVM garbage collection metrics.
     * Activated if {@code thor.metrics.collection.jvm} is true.
     *
     * @return MeterBinder for JVM GC metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "jvm", havingValue = "true", matchIfMissing = true)
    public JvmGcMetrics jvmGcMetrics() {
        return new JvmGcMetrics();
    }

    /**
     * Provides JVM thread metrics.
     * Activated if {@code thor.metrics.collection.jvm} is true.
     *
     * @return MeterBinder for JVM thread metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "jvm", havingValue = "true", matchIfMissing = true)
    public JvmThreadMetrics jvmThreadMetrics() {
        return new JvmThreadMetrics();
    }

    /**
     * Provides JVM class loader metrics.
     * Activated if {@code thor.metrics.collection.jvm} is true.
     *
     * @return MeterBinder for JVM class loader metrics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "jvm", havingValue = "true", matchIfMissing = true)
    public ClassLoaderMetrics classLoaderMetrics() {
        return new ClassLoaderMetrics();
    }

    /**
     * Provides database connection pool metrics.
     * Activated if a {@link DataSource} bean is present and
     * {@code thor.metrics.collection.database} is true.
     *
     * @param dataSource The auto-configured DataSource.
     * @return MeterBinder for database connection pool metrics.
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = "thor.metrics.collection", name = "database", havingValue = "true", matchIfMissing = true)
    public MeterBinder dataSourcePoolMetrics(DataSource dataSource) {
        return new DataSourcePoolMetrics(dataSource, Collections.emptyList());
    }

}
