# Mimir
<div align="center">
  <img width="440" alt="image" src="../doc/img/mimir.png" />
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
  - Ordre des méthodes
  - Documentation des méthodes
  - Documentation des types de retour
  - Documentation des exceptions
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
    
    @ApiDescription(
        value = "Description de la méthode",
        tags = {"tag1", "tag2"},
        category = "ma-categorie",
        order = 1,
        returnType = "String",
        throws_ = {
            @ApiDescription.Throws(
                exception = Exception.class,
                description = "Description de l'exception"
            )
        }
    )
    public String maMethode() throws Exception {
        // ...
    }

    @ApiDescription("Description du paramètre")
    public void autreMethode(@ApiDescription("Description du paramètre") String param) {
        // ...
    }
}
```

### Structure de l'annotation @ApiDescription

L'annotation `@ApiDescription` peut être utilisée sur les classes, méthodes et paramètres avec les attributs suivants :

#### Pour les classes et méthodes :
- `value()` : Description principale
- `tags()` : Tableau de tags pour la catégorisation
- `category()` : Catégorie de l'élément
- `order()` : Ordre d'affichage (par défaut : 0)
- `returnType()` : Type de retour (uniquement pour les méthodes)
- `throws_()` : Liste des exceptions possibles (uniquement pour les méthodes)

#### Pour les paramètres :
- `value()` : Description du paramètre

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
