package fr.eletutour.asgard.core;

public interface ChaosRule {
    String getName();
    boolean isEnabled();
    void applyChaos() throws Exception;
}
