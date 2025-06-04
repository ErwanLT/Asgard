package fr.eletutour.asgard.loki.model;

public class Munin {

    private Integer level;
    private boolean latencyActive;
    private Integer latencyRangeStart;
    private Integer latencyRangeEnd;
    private boolean exceptionActive;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean isLatencyActive() {
        return latencyActive;
    }

    public void setLatencyActive(boolean latencyActive) {
        this.latencyActive = latencyActive;
    }

    public Integer getLatencyRangeStart() {
        return latencyRangeStart;
    }

    public void setLatencyRangeStart(Integer latencyRangeStart) {
        this.latencyRangeStart = latencyRangeStart;
    }

    public Integer getLatencyRangeEnd() {
        return latencyRangeEnd;
    }

    public void setLatencyRangeEnd(Integer latencyRangeEnd) {
        this.latencyRangeEnd = latencyRangeEnd;
    }

    public boolean isExceptionActive() {
        return exceptionActive;
    }

    public void setExceptionActive(boolean exceptionActive) {
        this.exceptionActive = exceptionActive;
    }

    @Override
    public String toString() {
        return "Munin{" +
                "level=" + level +
                ", latencyActive=" + latencyActive +
                ", latencyRangeStart=" + latencyRangeStart +
                ", latencyRangeEnd=" + latencyRangeEnd +
                ", exceptionActive=" + exceptionActive +
                '}';
    }
}
