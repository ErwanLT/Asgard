package fr.eletutour.asgard.loki.aspect;

import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import fr.eletutour.asgard.loki.model.Hugin;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Random;

@Aspect
@Component
public class ChaosAspect {

    private final LokiChaos lokiChaos;
    private final Random random;

    public ChaosAspect(LokiChaos lokiChaos) {
        this.lokiChaos = lokiChaos;
        this.random = new Random();
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Repository *) || " +
            "within(@org.springframework.stereotype.Controller *)")
    public Object applyChaos(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!lokiChaos.isEnabled()) {
            return joinPoint.proceed();
        }

        Hugin watcher = lokiChaos.getWatcher();
        Munin chaosType = lokiChaos.getChaosType();

        // Vérifier si la couche est surveillée
        if (!isLayerWatched(joinPoint, watcher)) {
            return joinPoint.proceed();
        }

        // Vérifier si le chaos doit être appliqué selon le level
        if (random.nextInt(100) >= chaosType.getLevel()) {
            return joinPoint.proceed();
        }

        // Choisir aléatoirement entre latence et exception si les deux sont activés
        if (chaosType.isLatencyActive() && chaosType.isExceptionActive()) {
            if (random.nextBoolean()) {
                applyLatency(chaosType);
            } else {
                throw new RuntimeException("Chaos Engineering: Exception simulée");
            }
        } else if (chaosType.isLatencyActive()) {
            applyLatency(chaosType);
        } else if (chaosType.isExceptionActive()) {
            throw new RuntimeException("Chaos Engineering: Exception simulée");
        }

        return joinPoint.proceed();
    }

    private boolean isLayerWatched(ProceedingJoinPoint joinPoint, Hugin watcher) {
        String targetClassName = joinPoint.getTarget().getClass().getName().toLowerCase();
        
        if (targetClassName.contains("controller") && !targetClassName.contains("rest")) {
            return watcher.isController();
        } else if (targetClassName.contains("restcontroller")) {
            return watcher.isRestcontroller();
        } else if (targetClassName.contains("service")) {
            return watcher.isService();
        } else if (targetClassName.contains("repository")) {
            return watcher.isRepository();
        }
        
        return false;
    }

    private void applyLatency(Munin chaosType) throws InterruptedException {
        int minLatency = chaosType.getLatencyRangeStart();
        int maxLatency = chaosType.getLatencyRangeEnd();
        int latency = random.nextInt(maxLatency - minLatency + 1) + minLatency;
        Thread.sleep(latency);
    }
} 