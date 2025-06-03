# AuthorController

API de gestion des auteurs permettant de :
- Lister tous les auteurs
- Récupérer un auteur par son ID
- Créer un nouvel auteur
- Mettre à jour les informations d'un auteur
- Supprimer un auteur
- Cloner un auteur existant

Chaque auteur possède un nom, une biographie et peut être associé à plusieurs articles.


## Diagramme de Classe

```mermaid
classDiagram
    class AuthorController {
        -Logger log
        -AuthorService authorService
        +Author getAuthorById(Long arg0)
        +ResponseEntity createAuthor(String arg0, String arg1)
        +ResponseEntity updateAuthor(Long arg0, String arg1, String arg2)
        +ResponseEntity deleteAuthor(Long arg0)
        +ResponseEntity cloneAuthor(Long arg0)
        +ResponseEntity getAuthors()
    }

    AuthorController --> Logger : log
    AuthorController --> AuthorService : authorService
```

## Methods

### getAuthorById



#### Parameters

- `id` : ID de l'auteur à récupérer

#### Responses

- `200` : Auteur trouvé
- `404` : Auteur non trouvé
- `500` : Erreur interne du serveur

### createAuthor



#### Parameters

- `name` : Nom de l'auteur
- `bio` : Biographie de l'auteur

#### Responses

- `201` : Auteur créé avec succès
- `400` : Données invalides
- `500` : Erreur interne du serveur

### updateAuthor



#### Parameters

- `id` : ID de l'auteur à mettre à jour
- `name` : Nouveau nom de l'auteur
- `bio` : Nouvelle biographie de l'auteur

#### Responses

- `200` : Auteur mis à jour avec succès
- `404` : Auteur non trouvé
- `500` : Erreur interne du serveur

### deleteAuthor



#### Parameters

- `id` : ID de l'auteur à supprimer

#### Responses

- `204` : Auteur supprimé avec succès
- `404` : Auteur non trouvé
- `500` : Erreur interne du serveur

### cloneAuthor



#### Parameters

- `id` : ID de l'auteur à cloner

#### Responses

- `201` : Auteur cloné avec succès
- `404` : Auteur non trouvé
- `500` : Erreur interne du serveur

### getAuthors



#### Responses

- `200` : Liste des auteurs récupérée avec succès
- `404` : Auteurs non trouvés
- `500` : Erreur interne du serveur

