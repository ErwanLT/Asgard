# Asgard Core

## Description
Le module `asgard-core` est le cœur du framework Asgard. Il définit les interfaces et les classes de base pour la gestion des règles de chaos.

## Fonctionnalités principales
- Interface `ChaosRule` : Définit le contrat pour toutes les règles de chaos
- Gestion des états des règles (activé/désactivé)
- Système de nommage des règles
- Point d'entrée pour l'application des règles de chaos

## Utilisation
```java
public class MaRegle implements ChaosRule {
    @Override
    public void applyChaos() {
        // Implémentation de la règle
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "MaRegle";
    }
}
```

## Dépendances
- Spring Boot
- Java 17+

## Installation
```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>asgard-core</artifactId>
    <version>${asgard.version}</version>
</dependency>
``` 