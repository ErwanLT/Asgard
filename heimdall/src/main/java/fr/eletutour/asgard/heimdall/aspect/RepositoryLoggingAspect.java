package fr.eletutour.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "heimdall.logging.repository.enabled", havingValue = "true", matchIfMissing = false)
public class RepositoryLoggingAspect extends AbstractLoggingAspect{

    public RepositoryLoggingAspect() {
        logger.info("RepositoryLoggingAspect chargé");
    }

    @Around("within(@org.springframework.stereotype.Repository *)")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Repository");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("🗄️ [{}] Début de l'accès aux données - Méthode: {} - Classe: {} - Paramètres: {}", 
            annotationType, methodName, className, parameters);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("💾 [{}] Fin de l'accès aux données - Méthode: {} - Classe: {} - Résultat: {}", 
            annotationType, methodName, className, result);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("🚫 [{}] Erreur d'accès aux données - Méthode: {} - Classe: {} - Message: {}", 
            annotationType, methodName, className, e.getMessage());
    }
} 