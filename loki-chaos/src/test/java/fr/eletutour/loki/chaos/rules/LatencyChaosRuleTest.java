package fr.eletutour.loki.chaos.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LatencyChaosRuleTest {

    private LatencyChaosRule rule;

    @BeforeEach
    void setUp() {
        rule = new LatencyChaosRule();
        rule.setEnabled(true);
        rule.setDelayMsStart(100);
        rule.setDelayMsStop(200);
        rule.setTargetClass("com.example.TestService");
    }

    @Test
    void testIsEnabled() {
        assertThat(rule.isEnabled()).isTrue();
    }

    @Test
    void testGetName() {
        assertThat(rule.getName()).isEqualTo("LatencyChaos");
    }

    @Test
    void testGetTargetClass() {
        assertThat(rule.getTargetClass()).isEqualTo("com.example.TestService");
    }

    @Test
    void testApplyChaos() throws InterruptedException {
        long start = System.currentTimeMillis();
        rule.applyChaos();
        long end = System.currentTimeMillis();
        long duration = end - start;

        assertThat(duration).isGreaterThanOrEqualTo(100L);
        assertThat(duration).isLessThanOrEqualTo(200L);
    }
} 