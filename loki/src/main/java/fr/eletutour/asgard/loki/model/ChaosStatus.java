package fr.eletutour.asgard.loki.model;

import java.time.LocalDateTime;

public class ChaosStatus {
    private boolean enabled;
    private LocalDateTime enabledAt;

    public ChaosStatus(boolean enabled, LocalDateTime enabledAt) {
        this.enabled = enabled;
        this.enabledAt = enabledAt;
    }

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

    @Override
    public String toString() {
        return "ChaosStatus{" +
                "enabled=" + enabled +
                ", enabledAt=" + enabledAt +
                '}';
    }
}
