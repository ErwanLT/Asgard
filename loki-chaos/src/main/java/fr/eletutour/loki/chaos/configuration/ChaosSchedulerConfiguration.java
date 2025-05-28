package fr.eletutour.loki.chaos.configuration;

import fr.eletutour.asgard.core.ScheduledChaosRule;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;

@Configuration
@EnableScheduling
public class ChaosSchedulerConfiguration implements SchedulingConfigurer {

    private final List<ScheduledChaosRule> scheduledRules;

    public ChaosSchedulerConfiguration(List<ScheduledChaosRule> scheduledRules) {
        this.scheduledRules = scheduledRules;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        scheduledRules.forEach(rule -> {
            if (rule.isEnabled()) {
                taskRegistrar.addCronTask(() -> {
                    try {
                        rule.applyChaos();
                    } catch (Exception e) {
                        // Log l'erreur mais ne pas arrêter le scheduler
                        e.printStackTrace();
                    }
                }, rule.getCronExpression());
            }
        });
    }
} 