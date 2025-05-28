package fr.eletutour.asgard.heimdall.aspect;

import fr.eletutour.asgard.core.ChaosRule;
import fr.eletutour.asgard.heimdall.metrics.ChaosMetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChaosMetricsAspect {

    private final ChaosMetricsService metricsService;

    public ChaosMetricsAspect(ChaosMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Around("execution(* fr.eletutour.asgard.core.ChaosRule.applyChaos())")
    public Object aroundChaosRuleExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        ChaosRule rule = (ChaosRule) joinPoint.getTarget();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            metricsService.recordRuleExecution(rule, executionTime);
            return result;
        } catch (Throwable error) {
            metricsService.recordRuleFailure(rule, error);
            throw error;
        }
    }
} 