package fr.eletutour.asgard.baldr.controller;

import fr.eletutour.asgard.baldr.model.Article;
import fr.eletutour.asgard.baldr.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<Article> getArticles() {
        return articleService.getArticles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleByIdAvecTimeout(@PathVariable Long id) throws TimeoutException, InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Article> future = executor.submit(() -> articleService.getArticleById(id));

        try {
            Article article = future.get(2, TimeUnit.SECONDS); // Timeout de 2 secondes
            return ResponseEntity.ok(article);
        } finally {
            executor.shutdownNow();
        }
    }

}
