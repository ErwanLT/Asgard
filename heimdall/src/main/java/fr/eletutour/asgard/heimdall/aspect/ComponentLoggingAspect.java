package fr.eletutour.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "heimdall.logging.component.enabled", havingValue = "true", matchIfMissing = false)
public class ComponentLoggingAspect extends AbstractLoggingAspect {

    public ComponentLoggingAspect() {
        logger.info("ComponentLoggingAspect chargé");
    }

    @Around("within(@org.springframework.stereotype.Component *)")
    public Object logComponent(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Component");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("🔨 [{}] Début de l'exécution: {}#{}", annotationType, className, methodName);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("✨ [{}] Fin de l'exécution: {}#{}", annotationType, className, methodName);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("💢 [{}] Erreur d'exécution: {}#{} - Erreur: {}", annotationType, className, methodName, e.getMessage());
    }
}