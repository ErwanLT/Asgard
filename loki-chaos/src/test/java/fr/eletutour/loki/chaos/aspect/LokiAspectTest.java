package fr.eletutour.loki.chaos.aspect;

import fr.eletutour.asgard.core.JoinPointAwareChaosRule;
import fr.eletutour.loki.chaos.rules.LatencyChaosRule;
import fr.eletutour.loki.chaos.service.DummyService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LokiAspectTest {

    private LokiAspect lokiAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    private LatencyChaosRule latencyChaosRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        latencyChaosRule = new LatencyChaosRule();
        latencyChaosRule.setEnabled(true);
        latencyChaosRule.setDelayMsStart(1000);
        latencyChaosRule.setDelayMsStop(2000);
        latencyChaosRule.setTargetClass(DummyService.class.getName());

        List<JoinPointAwareChaosRule> rules = new ArrayList<>();
        rules.add(latencyChaosRule);
        lokiAspect = new LokiAspect(rules);
    }

    @Test
    void whenTargetClassMatches_thenLatencyRuleIsApplied() throws Throwable {
        // Arrange
        DummyService target = new DummyService();
        when(proceedingJoinPoint.getTarget()).thenReturn(target);
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        // Act
        long startTime = System.currentTimeMillis();
        lokiAspect.around(proceedingJoinPoint);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        verify(proceedingJoinPoint).proceed();
        assertTrue(duration >= 1000, "La durée devrait être d'au moins 1000ms");
        assertTrue(duration <= 2000, "La durée devrait être d'au maximum 2000ms");
    }

    @Test
    void whenTargetClassDoesNotMatch_thenLatencyRuleIsNotApplied() throws Throwable {
        // Arrange
        DummyService target = new DummyService();
        when(proceedingJoinPoint.getTarget()).thenReturn(target);
        when(proceedingJoinPoint.proceed()).thenReturn("result");
        latencyChaosRule.setTargetClass("DifferentClass");

        // Act
        long startTime = System.currentTimeMillis();
        lokiAspect.around(proceedingJoinPoint);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        verify(proceedingJoinPoint).proceed();
        assertTrue(duration < 100, "La durée devrait être inférieure à 100ms car la classe ne correspond pas");
    }

    @Test
    void whenLatencyRuleIsDisabled_thenNoDelayIsApplied() throws Throwable {
        // Arrange
        DummyService target = new DummyService();
        when(proceedingJoinPoint.getTarget()).thenReturn(target);
        when(proceedingJoinPoint.proceed()).thenReturn("result");
        latencyChaosRule.setEnabled(false);

        // Act
        long startTime = System.currentTimeMillis();
        lokiAspect.around(proceedingJoinPoint);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        verify(proceedingJoinPoint).proceed();
        assertTrue(duration < 1000, "La durée devrait être inférieure à 1000ms car la règle est désactivée");
    }
} 