package fr.eletutour.asgard.heimdall.metrics;

import fr.eletutour.asgard.core.ChaosRule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class ChaosMetricsService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Counter> ruleCounters;
    private final ConcurrentHashMap<String, Timer> ruleTimers;
    private final ConcurrentHashMap<String, Counter> failureCounters;

    public ChaosMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.ruleCounters = new ConcurrentHashMap<>();
        this.ruleTimers = new ConcurrentHashMap<>();
        this.failureCounters = new ConcurrentHashMap<>();
    }

    public void recordRuleExecution(ChaosRule rule, long executionTimeMs) {
        String ruleName = rule.getName();
        
        // Incrémenter le compteur d'exécutions
        Counter counter = ruleCounters.computeIfAbsent(ruleName,
            name -> Counter.builder("chaos.rule.executions")
                .tag("rule", name)
                .description("Nombre d'exécutions de la règle de chaos")
                .register(meterRegistry));
        counter.increment();

        // Enregistrer le temps d'exécution
        Timer timer = ruleTimers.computeIfAbsent(ruleName,
            name -> Timer.builder("chaos.rule.execution.time")
                .tag("rule", name)
                .description("Temps d'exécution de la règle de chaos")
                .register(meterRegistry));
        timer.record(executionTimeMs, TimeUnit.MILLISECONDS);
    }

    public void recordRuleFailure(ChaosRule rule, Throwable error) {
        String ruleName = rule.getName();
        String errorType = error.getClass().getSimpleName();
        String key = ruleName + ":" + errorType;
        
        Counter failureCounter = failureCounters.computeIfAbsent(key,
            k -> Counter.builder("chaos.rule.failures")
                .tag("rule", ruleName)
                .tag("error", errorType)
                .description("Nombre d'échecs de la règle de chaos")
                .register(meterRegistry));
        failureCounter.increment();
    }
} 