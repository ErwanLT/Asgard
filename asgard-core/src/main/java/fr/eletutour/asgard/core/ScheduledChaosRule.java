package fr.eletutour.asgard.core;

public interface ScheduledChaosRule extends ChaosRule {
    String getCronExpression(); // Ex: "0 0/5 * * * *"
}
