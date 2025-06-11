package fr.eletutour.asgard.hel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HelServiceTest {

    @Mock
    private TaskScheduler taskScheduler;

    private HelService helService;

    @BeforeEach
    void setUp() {
        helService = new HelService(taskScheduler);
    }

    @Test
    void shutdownImmediate_ShouldNotThrowException() {
        // Given
        // When
        helService.shutdownImmediate();

        // Then
        // Vérifie que la méthode ne lance pas d'exception
        // Note: System.exit(0) n'est pas testé car il arrêterait les tests
    }

    @Test
    void scheduleShutdown_WithValidCronExpression_ShouldScheduleTask() {
        // Given
        String validCronExpression = "0 0 0 * * ?";
        ScheduledFuture<Object> future = mock(ScheduledFuture.class);
        doReturn(future).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        // When
        helService.scheduleShutdown(validCronExpression);

        // Then
        ArgumentCaptor<CronTrigger> triggerCaptor = ArgumentCaptor.forClass(CronTrigger.class);
        verify(taskScheduler).schedule(any(Runnable.class), triggerCaptor.capture());
        
        CronTrigger capturedTrigger = triggerCaptor.getValue();
        assertEquals(validCronExpression, capturedTrigger.getExpression());
    }

    @Test
    void scheduleShutdown_WithExistingScheduledTask_ShouldCancelPreviousTask() {
        // Given
        String validCronExpression = "0 0 0 * * ?";
        ScheduledFuture<Object> future = mock(ScheduledFuture.class);
        doReturn(future).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        // When
        helService.scheduleShutdown(validCronExpression);
        helService.scheduleShutdown(validCronExpression);

        // Then
        verify(future).cancel(false);
        verify(taskScheduler, times(2)).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void scheduleShutdown_WithInvalidCronExpression_ShouldThrowException() {
        // Given
        String invalidCronExpression = "invalid cron";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> helService.scheduleShutdown(invalidCronExpression)
        );
        assertEquals("Cron expression must consist of 6 fields (found 2 in \"invalid cron\")", exception.getMessage());
    }

    @Test
    void scheduleShutdown_WithNullCronExpression_ShouldThrowException() {
        // Given
        String nullCronExpression = null;

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> helService.scheduleShutdown(nullCronExpression)
        );
    }

    @Test
    void scheduleShutdown_WithEmptyCronExpression_ShouldThrowException() {
        // Given
        String emptyCronExpression = "";

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> helService.scheduleShutdown(emptyCronExpression)
        );
    }
}