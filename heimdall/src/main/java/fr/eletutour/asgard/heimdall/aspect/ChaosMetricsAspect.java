package fr.eletutour.asgard.heimdall.aspect;

import fr.eletutour.asgard.core.ChaosRule;
import fr.eletutour.asgard.heimdall.logging.ChaosLoggingService;
import fr.eletutour.asgard.heimdall.metrics.ChaosMetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChaosMetricsAspect {

    private final ChaosMetricsService metricsService;
    private final ChaosLoggingService loggingService;

    public ChaosMetricsAspect(ChaosMetricsService metricsService, ChaosLoggingService loggingService) {
        this.metricsService = metricsService;
        this.loggingService = loggingService;
    }

    @Around("execution(* fr.eletutour.asgard.core.ChaosRule.applyChaos())")
    public Object aroundChaosRuleExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        ChaosRule rule = (ChaosRule) joinPoint.getTarget();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Enregistrer les métriques
            metricsService.recordRuleExecution(rule, executionTime);
            
            // Logger l'exécution
            loggingService.logRuleExecution(rule, executionTime);
            
            return result;
        } catch (Throwable error) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Enregistrer l'exécution même en cas d'échec
            metricsService.recordRuleExecution(rule, executionTime);
            
            // Enregistrer l'échec dans les métriques
            metricsService.recordRuleFailure(rule, error);
            
            // Logger l'échec
            loggingService.logRuleFailure(rule, error);
            
            throw error;
        }
    }
} 