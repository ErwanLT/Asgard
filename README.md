# Asgard

Asgard est une suite d'outils et de bibliothèques pour faciliter le développement d'applications Spring Boot.

## Modules

### Heimdall
Module de logging avancé pour Spring Boot qui permet de tracer automatiquement l'exécution des méthodes annotées avec les stéréotypes Spring courants.

[Documentation Heimdall](heimdall/README.md)

## Installation

### Prérequis
- Java 21 ou supérieur
- Maven 3.8 ou supérieur

### Installation des dépendances
```bash
mvn clean install
```

## Structure du projet

```
asgard/
├── heimdall/           # Module de logging
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   └── pom.xml
├── docs/              # Documentation
│   └── img/          # Images pour la documentation
├── pom.xml           # POM parent
└── README.md         # Ce fichier
```

## Développement

### Configuration de l'environnement
1. Cloner le repository
```bash
git clone https://github.com/votre-username/asgard.git
cd asgard
```

2. Installer les dépendances
```bash
mvn clean install
```

### Tests
Pour exécuter tous les tests :
```bash
mvn test
```

Pour exécuter les tests d'un module spécifique :
```bash
cd heimdall
mvn test
```

## Contribution

Les contributions sont les bienvenues ! Voici comment contribuer :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Commiter vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Pousser vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

### Standards de code
- Suivre les conventions de nommage Java
- Ajouter des tests unitaires pour les nouvelles fonctionnalités
- Mettre à jour la documentation
- Maintenir la couverture de code

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Contact

Ewan Le Tutour - [@ErwanLeTutour](https://x.com/ErwanLeTutour)

Lien du projet : [https://github.com/ErwanLT/Asgard](https://github.com/ErwanLT/Asgard)
