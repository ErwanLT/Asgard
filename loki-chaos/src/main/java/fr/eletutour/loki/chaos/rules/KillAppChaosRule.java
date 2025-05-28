package fr.eletutour.loki.chaos.rules;

import fr.eletutour.asgard.core.ScheduledChaosRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KillAppChaosRule implements ScheduledChaosRule {

    private static final Logger logger = LoggerFactory.getLogger(KillAppChaosRule.class);

    @Value("${chaos.kill.enabled:false}")
    private boolean enabled;

    @Value("${chaos.kill.cron:0 0/30 * * * *}") // chaque 30 minutes par défaut
    private String cron;

    @Override
    public void applyChaos() {
        logger.warn("KillAppChaosRule: shutting down the application as scheduled.");
        System.exit(1); // brutal mais explicite pour du chaos engineering
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "KillAppChaos";
    }

    @Override
    public String getCronExpression() {
        return cron;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
