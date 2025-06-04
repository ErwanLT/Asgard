package fr.eletutour.asgard.loki.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;

@ControllerAdvice
public class LokiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleExceptions(Exception e) {
        return switch (e) {
            case IllegalArgumentException ex -> buildProblemDetail(
                    HttpStatus.BAD_REQUEST,
                    "Configuration invalide",
                    "/loki/chaos",
                    "https://datatracker.ietf.org/doc/html/rfc7807",
                    ex.getMessage(),
                    Map.of()
            );
            case InterruptedException ex -> {
                Thread.currentThread().interrupt();
                yield buildProblemDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erreur interne",
                        "/loki/chaos",
                        "https://datatracker.ietf.org/doc/html/rfc7807",
                        "Le traitement a été interrompu",
                        Map.of()
                );
            }
            default -> buildProblemDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur interne",
                    "/loki/chaos",
                    "https://datatracker.ietf.org/doc/html/rfc7807",
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