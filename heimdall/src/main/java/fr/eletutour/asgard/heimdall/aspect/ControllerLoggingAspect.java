package fr.eletutour.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "heimdall.logging.controller.enabled", havingValue = "true", matchIfMissing = false)
public class ControllerLoggingAspect extends AbstractLoggingAspect {

    public ControllerLoggingAspect() {
        logger.info("ControllerLoggingAspect chargé");
    }

    @Around("within(@org.springframework.stereotype.Controller *)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Controller");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("📝 [{}] Début du rendu de vue - Méthode: {} - Classe: {} - Paramètres: {}", 
            annotationType, methodName, className, parameters);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("📄 [{}] Fin du rendu de vue - Méthode: {} - Classe: {} - Vue: {}", 
            annotationType, methodName, className, result);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("⚠️ [{}] Erreur de rendu - Méthode: {} - Classe: {} - Message: {}", 
            annotationType, methodName, className, e.getMessage());
    }
} 