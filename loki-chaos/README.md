# Loki Chaos
![loki.png](../docs/img/loki.png)
## Description
Le module `loki-chaos` est une implémentation spécifique des règles de chaos pour Loki, le système de logging de Grafana. Il permet d'injecter des perturbations contrôlées dans les logs pour tester la résilience des systèmes.

## Fonctionnalités principales
- Règles de chaos pour la latence des logs
- Règles de chaos pour les erreurs de logs
- Intégration avec Heimdall pour le monitoring
- Configuration flexible des règles

## Règles disponibles
- `LatencyChaosRule` : Ajoute un délai aléatoire à l'envoi des logs
- `ErrorChaosRule` : Injecte des erreurs aléatoires dans les logs

## Utilisation
```java
@Configuration
public class LokiConfiguration {
    @Bean
    public LatencyChaosRule latencyChaosRule() {
        return new LatencyChaosRule();
    }

    @Bean
    public ErrorChaosRule errorChaosRule() {
        return new ErrorChaosRule();
    }
}
```

## Dépendances
- Spring Boot
- Java 17+
- asgard-core
- heimdall

## Installation
```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>loki-chaos</artifactId>
    <version>${asgard.version}</version>
</dependency>
```

## Configuration
Les règles peuvent être configurées via les propriétés Spring Boot :

```yaml
loki:
  chaos:
    latency:
      enabled: true
      min-delay: 100
      max-delay: 1000
    error:
      enabled: true
      error-rate: 0.1
```

## Monitoring
Les métriques et logs sont automatiquement collectés par Heimdall et peuvent être visualisés via :
- Les endpoints Actuator
- Les logs de l'application
- Les métriques Micrometer 