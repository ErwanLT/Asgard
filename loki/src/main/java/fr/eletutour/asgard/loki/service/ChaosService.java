package fr.eletutour.asgard.loki.service;

import fr.eletutour.asgard.loki.model.ChaosStatus;
import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChaosService {

    private final LokiChaos chaos;

    public ChaosService() {
        this.chaos = new LokiChaos();
        this.chaos.setEnabled(false);
        this.chaos.setWatcher(new Hugin());
        this.chaos.setChaosType(new Munin());
    }

    public ChaosStatus enableChaos() {
        chaos.setEnabled(true);
        chaos.setEnabledAt(LocalDateTime.now());
        return new ChaosStatus(chaos.isEnabled(), chaos.getEnabledAt());
    }

    public ChaosStatus disableChaos() {
        chaos.setEnabled(false);
        chaos.setEnabledAt(null);
        return new ChaosStatus(chaos.isEnabled(), null);
    }

    public Hugin updateWatcher(Hugin watcher) {
        chaos.setWatcher(watcher);
        return chaos.getWatcher();
    }

    public Munin updateChaosType(Munin chaosType) {
        // Validation du level
        if (chaosType.getLevel() < 0 || chaosType.getLevel() > 100) {
            throw new IllegalArgumentException("Le level doit être compris entre 0 et 100");
        }
        
        // Validation des plages de latence
        if (chaosType.isLatencyActive()) {
            if (chaosType.getLatencyRangeStart() < 0 || chaosType.getLatencyRangeEnd() < 0) {
                throw new IllegalArgumentException("Les plages de latence doivent être positives");
            }
            if (chaosType.getLatencyRangeStart() > chaosType.getLatencyRangeEnd()) {
                throw new IllegalArgumentException("La plage de latence de début doit être inférieure à la plage de fin");
            }
        }

        chaos.setChaosType(chaosType);
        return chaos.getChaosType();
    }

    public LokiChaos getCurrentState() {
        return chaos;
    }
}
