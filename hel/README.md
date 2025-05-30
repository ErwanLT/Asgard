# Module Hel
<div align="center">
  <img width="440" alt="image" src="../docs/img/hel_1.png" />
</div>

Le module Hel est un composant Spring Boot qui fournit des API REST pour gérer l'arrêt d'une application de manière contrôlée. Il offre deux modes d'arrêt : immédiat et programmé.

## Fonctionnalités

- Arrêt immédiat de l'application
- Arrêt programmé avec expression cron
- Auto-configuration Spring Boot
- Intégration transparente dans n'importe quelle application Spring Boot

## Installation

Ajoutez la dépendance suivante dans votre `pom.xml` :

```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>hel</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Utilisation

### Arrêt immédiat

Pour arrêter l'application immédiatement :

```bash
curl -X POST http://localhost:8080/api/shutdown/immediate
```

### Arrêt programmé

Pour programmer un arrêt avec une expression cron :

```bash
curl -X POST "http://localhost:8080/api/shutdown/scheduled?cronExpression=0 0 0 * * ?"
```

#### Format des expressions cron

Les expressions cron suivent le format standard de Spring :
```
second minute hour day-of-month month day-of-week
```

Exemples :
- `0 0 0 * * ?` : tous les jours à minuit
- `0 0 12 * * ?` : tous les jours à midi
- `0 0 0 ? * MON` : tous les lundis à minuit

## Configuration

Le module s'auto-configure automatiquement lorsqu'il est ajouté comme dépendance. Aucune configuration supplémentaire n'est nécessaire.

## Sécurité

⚠️ **Attention** : Ces endpoints permettent d'arrêter l'application. Il est fortement recommandé de :
- Sécuriser ces endpoints avec Spring Security
- Limiter l'accès à ces endpoints aux administrateurs uniquement
- Ne pas exposer ces endpoints sur des environnements de production sans protection appropriée

## Dépendances

- Spring Boot Web
- Spring Boot Validation
- Spring Boot AutoConfigure

## Licence

Ce module fait partie du projet Asgard et est soumis aux mêmes conditions de licence.
