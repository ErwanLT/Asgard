# ArticleController

API de gestion des articles

## Diagramme de Classe

```mermaid
classDiagram
    class ArticleController {
        -Logger log
        -ArticleService articleService
        +ResponseEntity getArticleByIdAvecTimeout(Long arg0)
        +List getArticles()
    }
    ArticleController --> Logger : log
    ArticleController --> ArticleService : articleService
```

## Methods

### getArticleByIdAvecTimeout



#### Parameters

- `arg0` : ID de l'article à récupérer

#### Responses

- `200` : Article trouvé
- `404` : Article non trouvé
- `408` : Délai d'attente dépassé
- `500` : Erreur interne du serveur

### getArticles



#### Responses

- `200` : Liste des articles récupérée avec succès
- `500` : Erreur interne du serveur

