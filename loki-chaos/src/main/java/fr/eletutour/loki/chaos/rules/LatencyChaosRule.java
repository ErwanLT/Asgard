package fr.eletutour.loki.chaos.rules;

import fr.eletutour.asgard.core.JoinPointAwareChaosRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class LatencyChaosRule implements JoinPointAwareChaosRule {

    private static final Logger logger = LoggerFactory.getLogger(LatencyChaosRule.class);
    private final Random random = new Random();

    @Value("${chaos.latency.enabled:false}")
    private boolean enabled;

    @Value("${chaos.latency.delayMsStart:1000}")
    private long delayMsStart;

    @Value("${chaos.latency.delayMsStop:2000}")
    private long delayMsStop;

    @Value("${chaos.latency.targetClass:com.example.service.MyService}")
    private String targetClass;

    @Override
    public void applyChaos() throws InterruptedException {
        long delay = delayMsStart + (long) (random.nextDouble() * (delayMsStop - delayMsStart));
        logger.info("LatencyChaos: delaying {}ms on {}", delay, targetClass);
        Thread.sleep(delay);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "LatencyChaos";
    }

    @Override
    public String getTargetClass() {
        return targetClass;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDelayMsStart(long delayMsStart) {
        this.delayMsStart = delayMsStart;
    }

    public void setDelayMsStop(long delayMsStop) {
        this.delayMsStop = delayMsStop;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }
}
