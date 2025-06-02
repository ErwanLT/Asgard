package fr.eletutour.asgard.baldr.util;

import fr.eletutour.asgard.baldr.model.Article;
import fr.eletutour.asgard.baldr.model.Author;
import fr.eletutour.asgard.odin.patterns.creational.ReflectiveObjectPool;
import fr.eletutour.asgard.odin.patterns.creational.ReflectivePrototype;
import fr.eletutour.asgard.odin.patterns.creational.ReflectiveSingleton;

/**
 * Gestionnaire d'objets utilisant les patterns d'Odin.
 */
public class ObjectManager {
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final ReflectiveObjectPool<Article> articlePool;
    private static final ReflectivePrototype<Article> articlePrototype;
    private static final ReflectivePrototype<Author> authorPrototype;

    static {
        // Initialisation du pool d'articles
        articlePool = new ReflectiveObjectPool<>(
            Article.class,
            DEFAULT_POOL_SIZE,
            article -> {
                article.setId(null);
                article.setTitle(null);
                article.setContent(null);
                article.setAuthor(null);
            }
        );

        // Initialisation des prototypes
        articlePrototype = new ReflectivePrototype<>(new Article(), Article.class);
        authorPrototype = new ReflectivePrototype<>(new Author(), Author.class);
    }

    /**
     * Obtient un article du pool.
     *
     * @return Un article du pool
     */
    public static Article borrowArticle() {
        return articlePool.borrow();
    }

    /**
     * Retourne un article au pool.
     *
     * @param article L'article à retourner
     */
    public static void releaseArticle(Article article) {
        articlePool.release(article);
    }

    /**
     * Clone un article avec des personnalisations.
     *
     * @param article L'article à cloner
     * @return Un clone de l'article
     */
    public static Article cloneArticle(Article article) {
        return articlePrototype
            .customize("title", article.getTitle())
            .customize("content", article.getContent())
            .customize("author", article.getAuthor())
            .clone();
    }

    /**
     * Clone un auteur avec des personnalisations.
     *
     * @param author L'auteur à cloner
     * @return Un clone de l'auteur
     */
    public static Author cloneAuthor(Author author) {
        return authorPrototype
            .customize("name", author.getName())
            .customize("bio", author.getBio())
            .clone();
    }

    /**
     * Obtient une instance singleton d'une classe.
     *
     * @param <T> Le type de l'instance
     * @param clazz La classe dont on veut l'instance
     * @return L'instance singleton
     */
    public static <T> T getSingletonInstance(Class<T> clazz) {
        return ReflectiveSingleton.getInstance(clazz);
    }

    /**
     * Réinitialise le pool d'articles.
     */
    public static void resetArticlePool() {
        while (!articlePool.isEmpty()) {
            articlePool.release(articlePool.borrow());
        }
    }
} 