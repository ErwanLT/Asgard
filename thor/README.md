# Thor Module

Thor is a Spring Boot module designed for comprehensive performance monitoring and analysis within the Asgard ecosystem. It automatically collects a variety of metrics, provides configurable alerts for predefined thresholds, and integrates with monitoring systems like Prometheus.

## Features

-   Automated collection of system, JVM, HTTP endpoint, and database metrics.
-   Configurable alerting mechanism based on metric thresholds.
-   Integration with Prometheus for metrics scraping.
-   Highly configurable through standard Spring Boot properties.

## Getting Started

### Maven Dependency

To include Thor in your Spring Boot application, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>thor</artifactId>
    <version>1.0-SNAPSHOT</version> <!-- Use the appropriate version -->
</dependency>
```

### Basic Usage

Once the dependency is added, Thor's auto-configuration will activate by default. Metrics collection and alerting will start based on the default configuration. You can customize Thor's behavior extensively using the properties listed below in your `application.properties` or `application.yml` file.

For example, if Prometheus integration is enabled (which it is by default), Thor ensures that the `/actuator/prometheus` endpoint is available for scraping, provided `spring-boot-starter-actuator` is also on the classpath.

## Configuration Properties

Thor module uses the prefix `thor` for all its configuration properties.

### General Properties

-   `thor.enabled` (boolean, default: `true`): Globally enables or disables the Thor module. If `false`, no metrics will be collected, and no alerts will be checked or sent by Thor.

### Metrics Configuration (`thor.metrics.*`)

-   `thor.metrics.enabled` (boolean, default: `true`): Enables or disables all metric collection features of Thor. This is a master switch for metrics.
-   **Collection Settings (`thor.metrics.collection.*`)**
    -   `thor.metrics.collection.interval` (String/Duration, default: `30s`): Interval at which certain metrics are collected. This is also used as the default check interval for alerts. Examples: `30s`, `1m`, `500ms`.
    -   `thor.metrics.collection.endpoints` (boolean, default: `true`): Enable/disable collection of HTTP server request metrics (e.g., latency, count). Relies on Spring Boot Actuator's underlying mechanisms.
    -   `thor.metrics.collection.resources` (boolean, default: `true`): Enable/disable collection of system resource metrics, including CPU usage (`system.cpu.usage`) and JVM memory usage (`jvm.memory.used`, `jvm.memory.max`).
    -   `thor.metrics.collection.database` (boolean, default: `true`): Enable/disable collection of database connection pool metrics. Requires a `DataSource` bean to be present.
    -   `thor.metrics.collection.jvm` (boolean, default: `true`): Enable/disable collection of detailed JVM metrics, including garbage collection (`jvm.gc.*`), thread utilization (`jvm.threads.*`), and class loader metrics (`jvm.classes.*`).

### Alerts Configuration (`thor.alerts.*`)

-   `thor.alerts.enabled` (boolean, default: `true`): Enables or disables the alerting feature. If `false`, Thor will not check for or send any alerts.
-   `thor.alerts.channels` (List<String>, default: `["email", "slack"]`): Specifies the channels through which alerts should be sent. (Note: The actual sending mechanism for these channels is not yet implemented in this version of Thor; currently, alerts are logged as warnings). Example: `thor.alerts.channels=email,custom_webhook`
-   **Thresholds (`thor.alerts.thresholds.*`)**: Defines the thresholds for triggering alerts.
    -   `thor.alerts.thresholds.responseTime` (String/Duration, default: `1000ms`): Maximum acceptable average response time for HTTP requests. If breached, an alert is triggered. Example: `500ms`, `2s`.
    -   `thor.alerts.thresholds.cpuUsage` (String/Percentage, default: `80%`): Maximum acceptable CPU usage. If system CPU usage exceeds this value, an alert is triggered. Example: `75%`, `0.9` (for 90%).
    -   `thor.alerts.thresholds.memoryUsage` (String/Percentage, default: `85%`): Maximum acceptable heap memory usage (used vs. max). If JVM heap memory usage exceeds this percentage, an alert is triggered. Example: `80%`, `0.75` (for 75%).

### Integration Configuration (`thor.integration.*`)

-   **Prometheus (`thor.integration.prometheus.*`)**
    -   `thor.integration.prometheus.enabled` (boolean, default: `true`): Enables or disables integration with Prometheus. If `true`, Thor configures the Spring Boot Actuator Prometheus endpoint (`/actuator/prometheus`) to be enabled and exposed for scraping. If `false`, Thor ensures this endpoint is disabled.
-   **Grafana (`thor.integration.grafana.*`)**
    -   `thor.integration.grafana.enabled` (boolean, default: `true`): Placeholder for enabling/disabling Grafana integration features. (Note: Specific Grafana integration features, like automatic dashboard provisioning, are not yet implemented in this version of Thor).

## Contributing

[TODO: Add guidelines for contributing to the module.]

## License

This module is licensed under the MIT License. (Assuming MIT, update if different)
[TODO: Verify and ensure LICENSE file exists if referencing one.]
