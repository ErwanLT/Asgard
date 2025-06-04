package fr.eletutour.asgard.loki.controller;

import fr.eletutour.asgard.loki.exception.LokiControllerAdvice;
import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import fr.eletutour.asgard.loki.service.ChaosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class LokiControllerTest {

    private LokiController lokiController;
    private ChaosService chaosService;
    private LokiControllerAdvice controllerAdvice;

    @BeforeEach
    void setUp() {
        chaosService = new ChaosService();
        lokiController = new LokiController(chaosService);
        controllerAdvice = new LokiControllerAdvice();
    }

    @Test
    void whenEnableChaos_thenReturnsOk() {
        // When
        ResponseEntity<?> response = lokiController.enableChaos();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(chaosService.getCurrentState().isEnabled());
    }

    @Test
    void whenDisableChaos_thenReturnsOk() {
        // Given
        chaosService.enableChaos();

        // When
        ResponseEntity<?> response = lokiController.disableChaos();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(chaosService.getCurrentState().isEnabled());
    }

    @Test
    void whenUpdateWatcher_thenReturnsOk() {
        // Given
        Hugin newWatcher = new Hugin();
        newWatcher.setRestcontroller(true);
        newWatcher.setController(true);
        newWatcher.setService(true);
        newWatcher.setRepository(true);

        // When
        ResponseEntity<Hugin> response = lokiController.updateWatcher(newWatcher);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        Hugin updatedWatcher = response.getBody();
        assertNotNull(updatedWatcher);
        assertTrue(updatedWatcher.isRestcontroller());
        assertTrue(updatedWatcher.isController());
        assertTrue(updatedWatcher.isService());
        assertTrue(updatedWatcher.isRepository());
    }

    @Test
    void whenUpdateChaosType_thenReturnsOk() {
        // Given
        Munin newChaosType = new Munin();
        newChaosType.setLevel(50);
        newChaosType.setLatencyActive(true);
        newChaosType.setExceptionActive(true);
        newChaosType.setLatencyRangeStart(100);
        newChaosType.setLatencyRangeEnd(200);

        // When
        ResponseEntity<Munin> response = lokiController.updateChaosType(newChaosType);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        Munin updatedChaosType = response.getBody();
        assertNotNull(updatedChaosType);
        assertEquals(50, updatedChaosType.getLevel());
        assertTrue(updatedChaosType.isLatencyActive());
        assertTrue(updatedChaosType.isExceptionActive());
        assertEquals(100, updatedChaosType.getLatencyRangeStart());
        assertEquals(200, updatedChaosType.getLatencyRangeEnd());
    }

    @Test
    void whenUpdateChaosTypeWithInvalidLevel_thenReturnsBadRequest() {
        // Given
        Munin newChaosType = new Munin();
        newChaosType.setLevel(150); // Invalid level

        // When
        ProblemDetail problemDetail = controllerAdvice.handleExceptions(
            new IllegalArgumentException("Le level doit être compris entre 0 et 100")
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Configuration invalide", problemDetail.getTitle());
        assertEquals("/loki/chaos", problemDetail.getInstance().getPath());
        assertEquals("Le level doit être compris entre 0 et 100", problemDetail.getDetail());
    }

    @Test
    void whenUpdateChaosTypeWithInvalidLatencyRange_thenReturnsBadRequest() {
        // Given
        Munin newChaosType = new Munin();
        newChaosType.setLevel(22);
        newChaosType.setLatencyActive(true);
        newChaosType.setLatencyRangeStart(200);
        newChaosType.setLatencyRangeEnd(100); // Invalid range

        // When
        ProblemDetail problemDetail = controllerAdvice.handleExceptions(
            new IllegalArgumentException("La plage de latence de début doit être inférieure à la plage de fin")
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Configuration invalide", problemDetail.getTitle());
        assertEquals("/loki/chaos", problemDetail.getInstance().getPath());
        assertEquals("La plage de latence de début doit être inférieure à la plage de fin", problemDetail.getDetail());
    }

    @Test
    void whenGetCurrentState_thenReturnsCurrentState() {
        // Given
        chaosService.enableChaos();
        Hugin watcher = new Hugin();
        watcher.setRestcontroller(true);
        chaosService.updateWatcher(watcher);
        Munin chaosType = new Munin();
        chaosType.setLevel(50);
        chaosService.updateChaosType(chaosType);

        // When
        ResponseEntity<LokiChaos> response = lokiController.getCurrentState();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        LokiChaos currentState = response.getBody();
        assertNotNull(currentState);
        assertTrue(currentState.isEnabled());
        assertTrue(currentState.getWatcher().isRestcontroller());
        assertEquals(50, currentState.getChaosType().getLevel());
    }
} 