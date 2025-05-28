package fr.eletutour.asgard.heimdall.metrics;

import fr.eletutour.asgard.core.ChaosRule;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChaosMetricsServiceTest {

    private MeterRegistry meterRegistry;
    private ChaosMetricsService metricsService;

    @Mock
    private ChaosRule chaosRule;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new ChaosMetricsService(meterRegistry);
        when(chaosRule.getName()).thenReturn("TestRule");
    }

    @Test
    void shouldRecordRuleExecution() {
        // When
        metricsService.recordRuleExecution(chaosRule, 100);

        // Then
        assertThat(meterRegistry.get("chaos.rule.executions")
                .tag("rule", "TestRule")
                .counter()
                .count()).isEqualTo(1);
        assertThat(meterRegistry.get("chaos.rule.execution.time")
                .tag("rule", "TestRule")
                .timer()
                .count()).isEqualTo(1);
    }

    @Test
    void shouldRecordRuleFailure() {
        // When
        RuntimeException error = new RuntimeException("Test error");
        metricsService.recordRuleFailure(chaosRule, error);

        // Then
        assertThat(meterRegistry.get("chaos.rule.failures")
                .tag("rule", "TestRule")
                .tag("error", "RuntimeException")
                .counter()
                .count()).isEqualTo(1);
    }

    @Test
    void shouldRecordMultipleExecutions() {
        // When
        metricsService.recordRuleExecution(chaosRule, 100);
        metricsService.recordRuleExecution(chaosRule, 200);
        metricsService.recordRuleExecution(chaosRule, 300);

        // Then
        assertThat(meterRegistry.get("chaos.rule.executions")
                .tag("rule", "TestRule")
                .counter()
                .count()).isEqualTo(3);
        assertThat(meterRegistry.get("chaos.rule.execution.time")
                .tag("rule", "TestRule")
                .timer()
                .count()).isEqualTo(3);
    }
} 