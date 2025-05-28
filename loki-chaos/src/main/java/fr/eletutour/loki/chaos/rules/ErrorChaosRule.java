package fr.eletutour.loki.chaos.rules;

import fr.eletutour.asgard.core.JoinPointAwareChaosRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ErrorChaosRule implements JoinPointAwareChaosRule {

    private static final Logger logger = LoggerFactory.getLogger(ErrorChaosRule.class);
    private final Random random = new Random();

    @Value("${chaos.error.enabled:false}")
    private boolean enabled;

    @Value("${chaos.error.probability:0.5}")
    private double probability;

    @Value("${chaos.error.targetClass:com.example.service.MyService}")
    private String targetClass;

    @Override
    public void applyChaos() {
        if (random.nextDouble() < probability) {
            logger.info("ErrorChaos: throwing error on {} with probability {}", targetClass, probability);
            throw new RuntimeException("Chaos error injected");
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "ErrorChaos";
    }

    @Override
    public String getTargetClass() {
        return targetClass;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }
} 