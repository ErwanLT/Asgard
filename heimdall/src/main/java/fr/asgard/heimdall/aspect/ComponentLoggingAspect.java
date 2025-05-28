package fr.asgard.heimdall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ComponentLoggingAspect extends AbstractLoggingAspect {

    @Around("@annotation(org.springframework.stereotype.Component)")
    public Object logComponent(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Component");
    }

    @Override
    protected void logEntry(String annotationType, String methodName, String className, String parameters) {
        logger.info("🔨 [{}] Début de l'exécution - Méthode: {} - Classe: {} - Paramètres: {}", 
            annotationType, methodName, className, parameters);
    }

    @Override
    protected void logExit(String annotationType, String methodName, String className, String result) {
        logger.info("✨ [{}] Fin de l'exécution - Méthode: {} - Classe: {} - Résultat: {}", 
            annotationType, methodName, className, result);
    }

    @Override
    protected void logError(String annotationType, String methodName, String className, Exception e) {
        logger.error("💢 [{}] Erreur d'exécution - Méthode: {} - Classe: {} - Message: {}", 
            annotationType, methodName, className, e.getMessage());
    }
} 