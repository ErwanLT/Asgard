# Module Loki  - Chaos Engineering
<div align="center">
  <img width="440" alt="image" src="../docs/img/loki.png" />
</div>

Le module Loki est un outil de chaos engineering qui permet d'injecter des perturbations contrôlées dans une application Spring Boot pour tester sa résilience.

## Fonctionnalités

- Activation/désactivation du chaos engineering
- Configuration des couches à surveiller (Controllers, Services, Repositories)
- Types de perturbations :
    - Latence : ajout d'un délai aléatoire entre deux bornes
    - Exceptions : génération d'exceptions aléatoires
- Niveau de chaos ajustable (0-100)
- API REST pour la configuration en temps réel

## Installation

Ajoutez la dépendance suivante à votre `pom.xml` :

```xml
<dependency>
    <groupId>fr.eletutour</groupId>
    <artifactId>loki</artifactId>
    <version>${project.version}</version>
</dependency>
```

## Configuration

Le module Loki s'active automatiquement grâce à l'auto-configuration Spring Boot. Aucune configuration supplémentaire n'est nécessaire.

## API REST

### Activer le chaos
```http
POST /loki/chaos/enable
```

### Désactiver le chaos
```http
POST /loki/chaos/disable
```

### Mettre à jour les couches surveillées
```http
PUT /loki/chaos/watcher
Content-Type: application/json

{
    "restcontroller": true,
    "controller": true,
    "service": true,
    "repository": true
}
```

### Mettre à jour le type de chaos
```http
PUT /loki/chaos/type
Content-Type: application/json

{
    "level": 50,
    "latencyActive": true,
    "exceptionActive": true,
    "latencyRangeStart": 100,
    "latencyRangeEnd": 200
}
```

### Obtenir l'état actuel
```http
GET /loki/chaos/state
```

## Gestion des erreurs

Le module utilise `ProblemDetail` (RFC 7807) pour la gestion des erreurs. Les erreurs de configuration retournent un statut 400 avec les détails suivants :

```json
{
    "type": "about:blank",
    "title": "Configuration invalide",
    "status": 400,
    "detail": "Message d'erreur spécifique",
    "instance": "/loki/chaos"
}
```

## Tests

Le module inclut des tests unitaires complets pour :
- `ChaosAspect` : vérification de l'interception des méthodes
- `ChaosService` : validation de la logique métier
- `LokiController` : tests des endpoints REST

## Sécurité

Le module Loki est conçu pour être utilisé uniquement en environnement de développement et de test. Il est recommandé de :
- Ne pas l'activer en production
- Sécuriser les endpoints REST en production
- Utiliser des niveaux de chaos appropriés pour ne pas impacter les performances

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
- Ouvrir une issue pour signaler un bug
- Proposer une pull request pour une nouvelle fonctionnalité
- Améliorer la documentation