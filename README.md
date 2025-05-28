# Asgard

## Description
Asgard est un framework de chaos engineering pour Java, inspiré par la mythologie nordique. Il permet d'injecter des perturbations contrôlées dans les systèmes pour tester leur résilience.

## Architecture
Le projet est composé de trois modules principaux :

### [Asgard Core](asgard-core/README.md)
Le cœur du framework qui définit les interfaces et les classes de base pour la gestion des règles de chaos.

### [Heimdall](heimdall/README.md)
Le gardien d'Asgard, responsable de la surveillance et de la métrologie des règles de chaos. Il fournit des fonctionnalités de monitoring, de logging et de métriques.

### [Loki Chaos](loki-chaos/README.md)
Une implémentation spécifique des règles de chaos pour Loki, le système de logging de Grafana.

## Installation
```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>asgard-bom</artifactId>
    <version>${asgard.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## Prérequis
- Java 17+
- Maven 3.6+
- Spring Boot 3.x

## Build
```bash
mvn clean install
```

## Tests
```bash
mvn test
```

## Contribution
Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request

## Licence
Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Auteur
- Ewan Le Tutour
