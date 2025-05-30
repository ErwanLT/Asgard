package fr.eletutour.asgard.hel.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class HelService {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledShutdown;

    public HelService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void shutdownImmediate() {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Attendre 1 seconde pour permettre la réponse HTTP
                System.exit(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void scheduleShutdown(String cronExpression) {
        if (scheduledShutdown != null) {
            scheduledShutdown.cancel(false);
        }

        scheduledShutdown = taskScheduler.schedule(
                this::shutdownImmediate,
                new CronTrigger(cronExpression)
        );
    }
}
