# Heimdall
![heimdall.png](../docs/img/heimdall.png)
## Description
Le module `heimdall` est le gardien d'Asgard, responsable de la surveillance et de la métrologie des règles de chaos. Il fournit des fonctionnalités de monitoring, de logging et de métriques pour suivre l'exécution des règles de chaos.

## Fonctionnalités principales
- Collecte de métriques d'exécution des règles
- Logging détaillé des exécutions et des échecs
- Aspect pour l'interception automatique des règles
- Service de métriques avec Micrometer
- Service de logging personnalisé

## Métriques disponibles
- `chaos.rule.executions` : Nombre total d'exécutions par règle
- `chaos.rule.execution.time` : Temps d'exécution des règles
- `chaos.rule.failures` : Nombre d'échecs par règle et type d'erreur

## Utilisation
```java
@Configuration
public class MaConfiguration {
    @Bean
    public ChaosMetricsService chaosMetricsService(MeterRegistry meterRegistry) {
        return new ChaosMetricsService(meterRegistry);
    }

    @Bean
    public ChaosLoggingService chaosLoggingService() {
        return new ChaosLoggingService();
    }
}
```

## Dépendances
- Spring Boot
- Micrometer
- Java 17+
- asgard-core

## Installation
```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>heimdall</artifactId>
    <version>${asgard.version}</version>
</dependency>
```

## Configuration
Le module s'auto-configure automatiquement grâce à Spring Boot. Les métriques sont disponibles via l'endpoint `/actuator/metrics` si Spring Boot Actuator est configuré. 