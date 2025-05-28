# Asgard

Asgard est une suite d'outils et de bibliothèques pour le développement Spring Boot, inspirée par la mythologie nordique.

## Modules

### Heimdall
Module de logging avancé pour Spring Boot. [Documentation](heimdall/README.md)

### Mimir
Module de gestion des connaissances et de documentation automatique. [Documentation](mimir/README.md)

## Prérequis

- Java 21
- Maven 3.8+

## Installation

Pour utiliser un module dans votre projet, ajoutez la dépendance correspondante dans votre `pom.xml` :

```xml
<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>heimdall</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>fr.eletutour.asgard</groupId>
    <artifactId>mimir</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Structure du Projet

```
asgard/
├── heimdall/         # Module de logging
├── mimir/            # Module de documentation
└── pom.xml          # POM parent
```

## Développement

1. Cloner le repository :
```bash
git clone https://github.com/eletutour/asgard.git
cd asgard
```

2. Installer les dépendances :
```bash
mvn clean install
```

3. Exécuter les tests :
```bash
mvn test
```

## Contribution

Les contributions sont les bienvenues ! Voici comment contribuer :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/amazing-feature`)
3. Commit vos changements (`git commit -m 'Add some amazing feature'`)
4. Push vers la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

## Standards de Code

- Suivre les conventions de nommage Java
- Documenter le code avec Javadoc
- Écrire des tests unitaires
- Maintenir une couverture de code élevée
- Utiliser Lombok pour réduire le boilerplate

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Contact

Ewan Le Tutour - [@ErwanLeTutour](https://x.com/ErwanLeTutour)

Lien du projet : [https://github.com/ErwanLT/Asgard](https://github.com/ErwanLT/Asgard)
