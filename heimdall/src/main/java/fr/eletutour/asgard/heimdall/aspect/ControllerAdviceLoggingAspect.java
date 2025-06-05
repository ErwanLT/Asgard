package fr.eletutour.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "heimdall.logging.controlleradvice.enabled", havingValue = "true", matchIfMissing = false)
public class ControllerAdviceLoggingAspect extends AbstractLoggingAspect {

    public ControllerAdviceLoggingAspect() {
        logger.info("ControllerAdviceLoggingAspect chargé");
    }

    @Around("@within(org.springframework.web.bind.annotation.ControllerAdvice)")
    public Object logControllerAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "ControllerAdvice");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("⚠️ [{}] Début du traitement d'exception: {}#{} - Paramètres: {}", annotationType, className, methodName, parameters);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("✅ [{}] Fin du traitement d'exception: {}#{} - Résultat: {}", annotationType, className, methodName, result);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("💥 [{}] Erreur dans le traitement d'exception: {}#{} - Erreur: {}", annotationType, className, methodName, e.getMessage());
    }
}
