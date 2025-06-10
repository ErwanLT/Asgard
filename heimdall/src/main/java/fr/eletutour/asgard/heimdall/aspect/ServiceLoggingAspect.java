package fr.eletutour.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "heimdall.logging.service.enabled", havingValue = "true", matchIfMissing = false)
public class ServiceLoggingAspect extends AbstractLoggingAspect {

    public ServiceLoggingAspect() {
        logger.info("ServiceLoggingAspect chargé");
    }

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Service");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("⚙️ [{}] Début du traitement métier: {}#{} - Paramètres: {}", annotationType, className, methodName, parameters);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("🔧 [{}] Fin du traitement métier: {}#{} - Résultat: {}", annotationType, className, methodName, result);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("💥 [{}] Erreur métier: {}#{} - Erreur: {}", annotationType, className, methodName, e.getMessage());
    }
}