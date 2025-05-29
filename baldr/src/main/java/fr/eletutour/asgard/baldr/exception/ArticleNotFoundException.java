package fr.eletutour.asgard.baldr.exception;

public class ArticleNotFoundException extends Exception {

    private Long articleId;

    public ArticleNotFoundException(String msg, Long articleId) {
        super(msg);
        this.articleId = articleId;
    }


    public String getArticleId() {
        return articleId.toString();
    }
}
