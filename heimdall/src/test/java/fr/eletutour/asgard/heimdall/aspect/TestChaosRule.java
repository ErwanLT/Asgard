package fr.eletutour.asgard.heimdall.aspect;

import fr.eletutour.asgard.core.ChaosRule;

public class TestChaosRule implements ChaosRule {
    private boolean shouldThrow = false;
    private RuntimeException errorToThrow;

    @Override
    public void applyChaos() {
        if (shouldThrow && errorToThrow != null) {
            throw errorToThrow;
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "TestRule";
    }

    public void setShouldThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    public void setErrorToThrow(RuntimeException errorToThrow) {
        this.errorToThrow = errorToThrow;
    }
} 