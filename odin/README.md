# Odin
<div align="center">
  <img width="440" alt="image" src="../docs/img/odin.png" />
</div>

Odin est un framework Java qui implémente les patterns de conception classiques en utilisant la réflexion (reflection) pour offrir une approche plus flexible et dynamique.

## Architecture

Le projet est organisé en plusieurs packages correspondant aux différentes catégories de patterns :

### Patterns Comportementaux (Behavioral)

- **Observer** : `ReflectiveObserver`
  - Permet d'observer et de réagir aux changements d'état d'un objet
  - Utilise la réflexion pour appeler dynamiquement les méthodes de mise à jour

- **Visitor** : `ReflectiveVisitor`, `JsonVisitor`
  - Permet d'ajouter de nouvelles opérations à une hiérarchie de classes sans les modifier
  - Implémente un visiteur JSON pour la sérialisation d'objets

### Patterns Structurels (Structural)

- **Adapter** : `ReflectiveAdapter`
  - Permet d'adapter l'interface d'une classe à une autre interface
  - Utilise la réflexion pour mapper dynamiquement les méthodes

- **Decorator** : `ReflectiveDecorator`
  - Permet d'ajouter dynamiquement des responsabilités à un objet
  - Implémente des décorateurs courants : logging, cache, retry

- **Composite** : `ReflectiveComposite`
  - Permet de composer des objets en structures arborescentes
  - Gère dynamiquement les opérations sur les composants et les composites

### Patterns Créationnels (Creational)

- **Factory** : `ReflectiveFactory`
  - Permet de créer des objets sans spécifier leur classe exacte
  - Utilise la réflexion pour instancier dynamiquement les classes

- **Builder** : `ReflectiveBuilder`
  - Permet de construire des objets complexes étape par étape
  - Gère dynamiquement la construction des objets avec des paramètres nommés

## Utilisation

### Observer

```java
Subject subject = new Subject();
ReflectiveObserver observer = new ReflectiveObserver(subject, "update");
observer.update("Nouvelle valeur");
```

### Visitor

```java
JsonVisitor visitor = new JsonVisitor();
String json = visitor.visit(object);
```

### Adapter

```java
Adaptee adaptee = new Adaptee();
Target target = new ReflectiveAdapter<>(adaptee, Target.class).build();
```

### Decorator

```java
Service service = new ServiceImpl();
Service decoratedService = new ReflectiveDecorator<>(service, Service.class)
    .cache("getData")
    .logBefore("getData")
    .retry("getData", 3)
    .build();
```

### Composite

```java
Component component = new ReflectiveComposite<>(Component.class)
    .add(new Leaf())
    .add(new Leaf())
    .build();
```

### Factory

```java
Object instance = new ReflectiveFactory()
    .create("com.example.MyClass", "param1", "param2");
```

### Builder

```java
MyObject object = new ReflectiveBuilder<>(MyObject.class)
    .with("name", "value")
    .with("number", 42)
    .build();
```

## Tests

Le projet inclut une suite complète de tests unitaires pour chaque pattern. Les tests vérifient :

- Le comportement de base de chaque pattern
- Les cas d'erreur et les exceptions
- Les combinaisons de patterns
- Les performances et la robustesse

## Dépendances

- Java 21 ou supérieur
- JUnit 5 pour les tests

## Installation

```bash
git clone https://github.com/votre-username/Asgard.git
cd Asgard/odin
mvn install
```

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails. 