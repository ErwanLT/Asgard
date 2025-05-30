package fr.eletutour.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "heimdall.logging.rest.enabled", havingValue = "true", matchIfMissing = false)
public class RestControllerLoggingAspect extends AbstractLoggingAspect {

    public RestControllerLoggingAspect() {
        logger.info("RestControllerLoggingAspect chargé");
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRestController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "RestController");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("🚀 [{}] Début de l'appel API - Méthode: {} - Classe: {} - Paramètres: {}", 
            annotationType, methodName, className, parameters);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("✅ [{}] Fin de l'appel API - Méthode: {} - Classe: {} - Résultat: {}", 
            annotationType, methodName, className, result);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("❌ [{}] Erreur API - Méthode: {} - Classe: {} - Message: {}", 
            annotationType, methodName, className, e.getMessage());
    }
} 