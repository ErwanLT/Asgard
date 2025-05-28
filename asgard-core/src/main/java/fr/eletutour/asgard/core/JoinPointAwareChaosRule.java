package fr.eletutour.asgard.core;

public interface JoinPointAwareChaosRule extends ChaosRule {
    String getTargetClass(); // ou une expression SpEL plus avancée dans le futur
}
