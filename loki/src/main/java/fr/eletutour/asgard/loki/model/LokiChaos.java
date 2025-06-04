package fr.eletutour.asgard.loki.model;

import java.time.LocalDateTime;

public class LokiChaos {
    private boolean enabled;
    private LocalDateTime enabledAt;
    private Hugin watcher;
    private Munin chaosType;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getEnabledAt() {
        return enabledAt;
    }

    public void setEnabledAt(LocalDateTime enabledAt) {
        this.enabledAt = enabledAt;
    }

    public Hugin getWatcher() {
        return watcher;
    }

    public void setWatcher(Hugin watcher) {
        this.watcher = watcher;
    }

    public Munin getChaosType() {
        return chaosType;
    }

    public void setChaosType(Munin chaosType) {
        this.chaosType = chaosType;
    }

    @Override
    public String toString() {
        return "LokiChaos{" +
                "enabled=" + enabled +
                ", enabledAt=" + enabledAt +
                ", watcher=" + watcher +
                ", chaosType=" + chaosType +
                '}';
    }
}
