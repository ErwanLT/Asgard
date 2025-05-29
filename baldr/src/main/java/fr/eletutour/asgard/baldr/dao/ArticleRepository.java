package fr.eletutour.asgard.baldr.dao;

import fr.eletutour.asgard.baldr.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
