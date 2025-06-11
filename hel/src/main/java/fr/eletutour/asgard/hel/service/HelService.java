package fr.eletutour.asgard.hel.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

/**
 * Service responsable de la gestion de l'arrêt de l'application.
 * Ce service offre deux modes d'arrêt :
 * <ul>
 *     <li>Un arrêt immédiat avec un délai de 1 seconde</li>
 *     <li>Un arrêt programmé basé sur une expression cron</li>
 * </ul>
 */
@Service
public class HelService {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledShutdown;

    /**
     * Constructeur du service.
     *
     * @param taskScheduler Le planificateur de tâches utilisé pour les arrêts programmés
     */
    public HelService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Arrête l'application immédiatement après un délai de 1 seconde.
     * <br>
     * Le délai permet de s'assurer que la réponse HTTP est envoyée avant l'arrêt.
     * <br>
     * L'arrêt est effectué via System.exit(0).
     */
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

    /**
     * Programme l'arrêt de l'application selon une expression cron.
     * <br>
     * Si un arrêt était déjà programmé, il est annulé avant d'en programmer un nouveau.
     *
     * @param cronExpression L'expression cron définissant quand l'application doit s'arrêter
     *                      Format: "second minute hour day-of-month month day-of-week"
     *                      Exemple: "0 0 0 * * ?" pour un arrêt tous les jours à minuit
     * @throws IllegalArgumentException si l'expression cron est invalide
     */
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
