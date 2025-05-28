# Mimir
<div align="center">
  <img width="440" alt="image" src="../docs/img/mimir.png" />
</div>


Module de documentation automatique pour Spring Boot, inspiré par Mimir, le dieu de la sagesse et de la connaissance dans la mythologie nordique.

## Fonctionnalités Actuelles

### Documentation Automatique
- Génération automatique de documentation au format Markdown
- Support des annotations `@ApiDescription` pour documenter les classes et méthodes
- Génération de documentation structurée avec :
  - Description de la classe
  - Tags
  - Catégorie
  - Documentation des méthodes
  - Documentation des paramètres
  - Documentation des types de retour
  - Documentation des exceptions
- Génération automatique de diagrammes UML pour les classes
- Stockage de la documentation dans Elasticsearch
- Configuration via application.yml
- Intégration avec Spring Boot
- Validation des annotations au démarrage
- Formatage automatique du Markdown
- Logging des opérations

## Installation

Ajoutez la dépendance suivante à votre `pom.xml` :

```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>mimir</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Utilisation

### Documentation des Classes et Méthodes

Utilisez l'annotation `@ApiDescription` pour documenter vos classes et méthodes :

```java
@ApiDescription(
    value = "Description de la classe",
    tags = {"tag1", "tag2"},
    category = "ma-categorie"
)
public class MaClasse {
    
    @ApiDescription("Description de la méthode")
    public void maMethode() {
        // ...
    }
}
```

### Configuration

Configurez le module dans votre `application.yml` :

```yaml
mimir:
  documentation:
    output-dir: docs/
    format: markdown
    languages:
      - java
      - kotlin
```

## Roadmap

### Documentation Automatique (En cours)
- [x] Génération de documentation Markdown
- [x] Documentation des paramètres de méthodes
- [x] Documentation des types de retour
- [x] Support des diagrammes UML
- [x] Documentation des exceptions
- [x] Stockage dans Elasticsearch
- [ ] Interface REST pour accéder à la documentation
- [ ] Recherche en texte intégral dans la documentation
- [x] Filtrage par tags et catégories
- [ ] Export en différents formats (PDF, HTML)

### Knowledge Base (Planifié)
- [ ] Base de connaissances centralisée
- [ ] Articles techniques
- [ ] Guides de démarrage
- [ ] FAQ
- [ ] Solutions aux problèmes courants
- [ ] Documentation interactive
- [ ] Exemples de code
- [ ] Tutoriels vidéo

### Analyse de Code (Planifié)
- [ ] Détection de patterns
- [ ] Identification d'anti-patterns
- [ ] Suggestions d'amélioration
- [ ] Métriques de qualité de code
- [ ] Couverture de tests
- [ ] Analyse de complexité cyclomatique
- [ ] Détection de code mort
- [ ] Analyse de dépendances

### Intégration (Planifié)
- [ ] Intégration avec Jira
- [ ] Intégration avec Confluence
- [ ] Intégration avec GitHub/GitLab
- [ ] Notifications Slack
- [ ] Intégration avec Swagger/OpenAPI
- [ ] Intégration avec Postman
- [ ] Intégration avec VS Code
- [ ] Intégration avec IntelliJ IDEA

### Recherche et Navigation (Planifié)
- [ ] Recherche en texte intégral
- [x] Navigation par tags et catégories
- [ ] Suggestions de documentation pertinente
- [ ] Historique des modifications
- [ ] Recherche sémantique
- [ ] Auto-complétion
- [ ] Navigation par graphe de dépendances
- [ ] Filtres avancés

## Tests

Le module inclut une suite complète de tests unitaires pour vérifier :
- La génération correcte de la documentation
- La gestion des annotations
- Le formatage du contenu
- Les cas d'erreur

## Contribution

Les contributions sont les bienvenues ! Consultez le [README principal](../../README.md) pour les instructions de contribution.

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](../../LICENSE) pour plus de détails. 
