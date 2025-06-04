package fr.eletutour.asgard.loki.service;

import fr.eletutour.asgard.loki.model.ChaosStatus;
import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChaosService {

    private LokiChaos chaos;

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
}
