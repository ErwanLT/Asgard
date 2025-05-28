package fr.eletutour.asgard.heimdall.aspect;

import fr.eletutour.asgard.core.ChaosRule;
import fr.eletutour.asgard.heimdall.logging.ChaosLoggingService;
import fr.eletutour.asgard.heimdall.metrics.ChaosMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChaosMetricsAspectTest {

    private MeterRegistry meterRegistry;
    private ChaosMetricsService metricsService;
    private ChaosMetricsAspect aspect;

    @Mock
    private ChaosLoggingService loggingService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new ChaosMetricsService(meterRegistry);
        aspect = new ChaosMetricsAspect(metricsService, loggingService);
    }

    @Test
    void aspectShouldBeProperlyConfigured() {
        // Given
        TestChaosRule rule = new TestChaosRule();
        AspectJProxyFactory factory = new AspectJProxyFactory(rule);
        factory.addAspect(aspect);
        ChaosRule proxy = factory.getProxy();

        // When/Then
        assertThat(proxy).isNotNull();
        assertThat(proxy).isNotSameAs(rule);
    }

    @Test
    void shouldRecordSuccessfulExecution() throws Throwable {
        // Given
        TestChaosRule rule = new TestChaosRule();
        AspectJProxyFactory factory = new AspectJProxyFactory(rule);
        factory.addAspect(aspect);
        ChaosRule proxy = factory.getProxy();

        // When
        proxy.applyChaos();

        // Then
        verify(loggingService, times(1)).logRuleExecution(eq(rule), anyLong());
        
        // Vérifier les métriques d'exécution réussie
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
    void shouldRecordFailedExecution() throws InterruptedException {
        // Given
        TestChaosRule rule = new TestChaosRule();
        RuntimeException error = new RuntimeException("Test error");
        rule.setShouldThrow(true);
        rule.setErrorToThrow(error);

        AspectJProxyFactory factory = new AspectJProxyFactory(rule);
        factory.addAspect(aspect);
        ChaosRule proxy = factory.getProxy();

        // When
        assertThrows(RuntimeException.class, proxy::applyChaos);
        verify(loggingService, times(1)).logRuleFailure(eq(rule), eq(error));
        
        // Attendre un peu pour s'assurer que les métriques sont créées
        TimeUnit.MILLISECONDS.sleep(100);
        
        // Vérifier les métriques d'échec
        assertThat(meterRegistry.get("chaos.rule.executions")
                .tag("rule", "TestRule")
                .counter()
                .count()).isEqualTo(1);
        assertThat(meterRegistry.get("chaos.rule.failures")
                .tag("rule", "TestRule")
                .tag("error", "RuntimeException")
                .counter()
                .count()).isEqualTo(1);
    }

    // Classe de test pour simuler une règle de chaos
    private static class TestChaosRule implements ChaosRule {
        private boolean shouldThrow = false;
        private RuntimeException errorToThrow;

        @Override
        public void applyChaos() {
            if (shouldThrow) {
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
} 