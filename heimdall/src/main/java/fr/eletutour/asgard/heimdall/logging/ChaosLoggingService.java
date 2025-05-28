package fr.eletutour.asgard.heimdall.logging;

import fr.eletutour.asgard.core.ChaosRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChaosLoggingService {
    private static final Logger logger = LoggerFactory.getLogger(ChaosLoggingService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final Map<String, ChaosRuleExecution> ruleExecutions;

    public ChaosLoggingService() {
        this.ruleExecutions = new ConcurrentHashMap<>();
    }

    public void logRuleExecution(ChaosRule rule, long executionTimeMs) {
        String timestamp = LocalDateTime.now().format(formatter);
        String ruleName = rule.getName();
        
        // Log détaillé
        logger.info("Chaos Rule Execution - Rule: {}, Time: {}, Duration: {}ms, Status: SUCCESS",
            ruleName, timestamp, executionTimeMs);

        // Stocker l'exécution pour l'historique
        ruleExecutions.put(timestamp + "_" + ruleName, 
            new ChaosRuleExecution(ruleName, timestamp, executionTimeMs, true, null));
    }

    public void logRuleFailure(ChaosRule rule, Throwable error) {
        String timestamp = LocalDateTime.now().format(formatter);
        String ruleName = rule.getName();
        
        // Log détaillé de l'erreur
        logger.error("Chaos Rule Failure - Rule: {}, Time: {}, Error: {}",
            ruleName, timestamp, error.getMessage(), error);

        // Stocker l'échec pour l'historique
        ruleExecutions.put(timestamp + "_" + ruleName,
            new ChaosRuleExecution(ruleName, timestamp, 0, false, error));
    }

    public void logRuleStateChange(ChaosRule rule, boolean newState) {
        String timestamp = LocalDateTime.now().format(formatter);
        String ruleName = rule.getName();
        
        logger.info("Chaos Rule State Change - Rule: {}, Time: {}, New State: {}",
            ruleName, timestamp, newState ? "ENABLED" : "DISABLED");
    }

    public void logRuleConfiguration(ChaosRule rule, Map<String, Object> configuration) {
        String timestamp = LocalDateTime.now().format(formatter);
        String ruleName = rule.getName();
        
        logger.info("Chaos Rule Configuration - Rule: {}, Time: {}, Configuration: {}",
            ruleName, timestamp, configuration);
    }

    public Map<String, ChaosRuleExecution> getRuleExecutions() {
        return ruleExecutions;
    }

    public static class ChaosRuleExecution {
        private final String ruleName;
        private final String timestamp;
        private final long executionTimeMs;
        private final boolean success;
        private final Throwable error;

        public ChaosRuleExecution(String ruleName, String timestamp, long executionTimeMs, 
                                boolean success, Throwable error) {
            this.ruleName = ruleName;
            this.timestamp = timestamp;
            this.executionTimeMs = executionTimeMs;
            this.success = success;
            this.error = error;
        }

        // Getters
        public String getRuleName() { return ruleName; }
        public String getTimestamp() { return timestamp; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public boolean isSuccess() { return success; }
        public Throwable getError() { return error; }
    }
} 