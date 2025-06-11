package fr.eletutour.asgard.baldr.service;

import fr.eletutour.asgard.baldr.dao.ArticleRepository;
import fr.eletutour.asgard.baldr.exception.ArticleNotFoundException;
import fr.eletutour.asgard.baldr.exception.AuthorNotFoundException;
import fr.eletutour.asgard.baldr.model.Article;
import fr.eletutour.asgard.baldr.model.Author;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant les opérations liées aux articles.
 */
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    private final AuthorService authorService;

    /**
     * Constructeur du service ArticleService.
     *
     * @param articleRepository Le repository pour accéder aux données des articles.
     * @param authorService Le service pour accéder aux données des auteurs.
     */
    public ArticleService(ArticleRepository articleRepository, AuthorService authorService) {
        this.articleRepository = articleRepository;
        this.authorService = authorService;
    }

    /**
     * Récupère la liste de tous les articles.
     *
     * @return La liste de tous les articles.
     */
    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    /**
     * Récupère un article par son identifiant.
     *
     * @param id L'identifiant de l'article.
     * @return L'article correspondant à l'identifiant.
     * @throws ArticleNotFoundException Si aucun article n'est trouvé avec l'identifiant spécifié.
     */
    @Transactional(readOnly = true)
    public Article getArticleById(Long id) throws ArticleNotFoundException {
        return articleRepository.findById(id).orElseThrow( () -> new ArticleNotFoundException("Article non trouvé pour l'id : " + id, id));
    }

    /**
     * Crée un nouvel article.
     *
     * @param title Le titre de l'article.
     * @param content Le contenu de l'article.
     * @param authorId L'identifiant de l'auteur de l'article.
     * @return L'article créé.
     * @throws AuthorNotFoundException Si aucun auteur n'est trouvé avec l'identifiant spécifié.
     */
    @Transactional
    public Article createArticle(String title, String content, Long authorId) throws AuthorNotFoundException {
        Author author = authorService.getAuthorById(authorId);
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setAuthor(author);

        return articleRepository.save(article);
    }

    @PostConstruct
    private void init() throws AuthorNotFoundException {
        createArticle("Harry Potter", "Harry Potter is a series of seven fantasy novels written by British author J. K. Rowling.", 1L);
        createArticle("The Shining", "The Shining is a horror novel by American author Stephen King.", 2L);
    }

    @Transactional
    public Article updateArticle(Long id, String title, String content) throws ArticleNotFoundException {
        return articleRepository.findById(id)
            .orElseThrow(() -> new ArticleNotFoundException("Article non trouvé", id));
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

}