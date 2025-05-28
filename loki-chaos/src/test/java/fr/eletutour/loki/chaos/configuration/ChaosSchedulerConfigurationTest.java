package fr.eletutour.loki.chaos.configuration;

import fr.eletutour.asgard.core.ScheduledChaosRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChaosSchedulerConfigurationTest {

    private ChaosSchedulerConfiguration schedulerConfiguration;

    @Mock
    private ScheduledTaskRegistrar taskRegistrar;

    @Mock
    private ScheduledChaosRule mockRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<ScheduledChaosRule> rules = new ArrayList<>();
        rules.add(mockRule);
        schedulerConfiguration = new ChaosSchedulerConfiguration(rules);
    }

    @Test
    void whenRuleIsEnabled_thenTaskIsScheduled() {
        // Arrange
        when(mockRule.isEnabled()).thenReturn(true);
        when(mockRule.getCronExpression()).thenReturn("0 0/5 * * * *");

        // Act
        schedulerConfiguration.configureTasks(taskRegistrar);

        // Assert
        verify(taskRegistrar).addCronTask(any(Runnable.class), eq("0 0/5 * * * *"));
    }

    @Test
    void whenRuleIsDisabled_thenTaskIsNotScheduled() {
        // Arrange
        when(mockRule.isEnabled()).thenReturn(false);

        // Act
        schedulerConfiguration.configureTasks(taskRegistrar);

        // Assert
        verify(taskRegistrar, never()).addCronTask(any(Runnable.class), anyString());
    }

    @Test
    void whenRuleThrowsException_thenSchedulerContinues() throws Exception {
        // Arrange
        when(mockRule.isEnabled()).thenReturn(true);
        when(mockRule.getCronExpression()).thenReturn("0 0/5 * * * *");
        doThrow(new RuntimeException("Test exception")).when(mockRule).applyChaos();

        // Act & Assert
        schedulerConfiguration.configureTasks(taskRegistrar);
        // Si on arrive ici, c'est que l'exception n'a pas arrêté le scheduler
        verify(taskRegistrar).addCronTask(any(Runnable.class), eq("0 0/5 * * * *"));
    }
} 