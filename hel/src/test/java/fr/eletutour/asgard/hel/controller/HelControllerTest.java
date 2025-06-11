package fr.eletutour.asgard.hel.controller;

import fr.eletutour.asgard.hel.service.HelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HelControllerTest {

    @Mock
    private HelService helService;

    private HelController helController;

    @BeforeEach
    void setUp() {
        helController = new HelController(helService);
    }

    @Test
    void shutdownImmediate_ShouldReturnOkResponse() {
        // Given
        doNothing().when(helService).shutdownImmediate();

        // When
        ResponseEntity<String> response = helController.shutdownImmediate();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application shutdown initiated", response.getBody());
        verify(helService).shutdownImmediate();
    }

    @Test
    void shutdownScheduled_WithValidCronExpression_ShouldReturnOkResponse() {
        // Given
        String cronExpression = "0 0 0 * * ?";
        doNothing().when(helService).scheduleShutdown(cronExpression);

        // When
        ResponseEntity<String> response = helController.shutdownScheduled(cronExpression);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Scheduled shutdown configured with cron: " + cronExpression, response.getBody());
        verify(helService).scheduleShutdown(cronExpression);
    }

    @Test
    void shutdownScheduled_WithInvalidCronExpression_ShouldPropagateException() {
        // Given
        String invalidCronExpression = "invalid cron";
        doThrow(new IllegalArgumentException("Invalid cron expression"))
            .when(helService).scheduleShutdown(invalidCronExpression);

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> helController.shutdownScheduled(invalidCronExpression)
        );
    }
} 