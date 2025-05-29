package fr.eletutour.asgard.baldr.service;

import fr.eletutour.asgard.baldr.dao.ArticleRepository;
import fr.eletutour.asgard.baldr.exception.ArticleNotFoundException;
import fr.eletutour.asgard.baldr.exception.AuthorNotFoundException;
import fr.eletutour.asgard.baldr.model.Article;
import fr.eletutour.asgard.baldr.model.Author;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    private final AuthorService authorService;

    public ArticleService(ArticleRepository articleRepository, AuthorService authorService) {
        this.articleRepository = articleRepository;
        this.authorService = authorService;
    }

    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    public Article getArticleById(Long id) throws ArticleNotFoundException {
        return articleRepository.findById(id).orElseThrow( () -> new ArticleNotFoundException("Article non trouvé pour l'id : " + id, id));
    }

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
}