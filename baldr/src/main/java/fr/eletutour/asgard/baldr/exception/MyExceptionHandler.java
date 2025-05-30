package fr.eletutour.asgard.baldr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleExceptions(Exception e) {
        return switch (e) {
            case AuthorNotFoundException ex -> buildProblemDetail(
                    HttpStatus.NOT_FOUND,
                    "Author Not Found",
                    "/authors/" + ex.getAuthorId(),
                    "http://localhost:8089/docs/errors/author-not-found.html",
                    ex.getMessage(),
                    Map.of()
            );
            case ArticleNotFoundException ex -> buildProblemDetail(
                    HttpStatus.NOT_FOUND,
                    "Article Not Found",
                    "/articles/" + ex.getArticleId(),
                    "http://localhost:8089/docs/errors/article-not-found.html",
                    ex.getMessage(),
                    Map.of()
            );
            case TimeoutException ex -> buildProblemDetail(
                    HttpStatus.REQUEST_TIMEOUT,
                    "Time Out",
                    "",
                    "",
                    ex.getMessage(),
                    Map.of()
            );
            case InterruptedException ex -> {
                Thread.currentThread().interrupt();
                yield buildProblemDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erreur interne",
                        "",
                        "",
                        "Le traitement a été interrompu",
                        Map.of()
                );
            }
            case MethodArgumentNotValidException ex -> {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
                yield buildProblemDetail(
                        HttpStatus.BAD_REQUEST,
                        "Validation error",
                        "",
                        "https://datatracker.ietf.org/doc/html/rfc7807",
                        "Un ou plusieurs champs sont invalides.",
                        errors
                );
            }
            default -> buildProblemDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur interne",
                    "",
                    "",
                    "Une erreur est survenue",
                    Map.of("error", e.getMessage())
            );
        };
    }

    private ProblemDetail buildProblemDetail(HttpStatus httpStatus, String title, String instance, String type, String detail, Map<String, String> properties) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(instance));
        problemDetail.setType(URI.create(type));
        problemDetail.setDetail(detail);
        properties.forEach(problemDetail::setProperty);
        return problemDetail;
    }

}