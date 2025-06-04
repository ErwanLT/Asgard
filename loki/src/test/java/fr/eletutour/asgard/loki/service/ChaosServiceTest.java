package fr.eletutour.asgard.loki.service;

import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChaosServiceTest {

    private ChaosService chaosService;

    @BeforeEach
    void setUp() {
        chaosService = new ChaosService();
    }

    @Test
    void whenEnableChaos_thenChaosIsEnabled() {
        // When
        chaosService.enableChaos();

        // Then
        assertTrue(chaosService.getCurrentState().isEnabled());
    }

    @Test
    void whenDisableChaos_thenChaosIsDisabled() {
        // Given
        chaosService.enableChaos();

        // When
        chaosService.disableChaos();

        // Then
        assertFalse(chaosService.getCurrentState().isEnabled());
    }

    @Test
    void whenUpdateWatcher_thenWatcherIsUpdated() {
        // Given
        Hugin newWatcher = new Hugin();
        newWatcher.setRestcontroller(true);
        newWatcher.setController(true);
        newWatcher.setService(true);
        newWatcher.setRepository(true);

        // When
        Hugin updatedWatcher = chaosService.updateWatcher(newWatcher);

        // Then
        assertTrue(updatedWatcher.isRestcontroller());
        assertTrue(updatedWatcher.isController());
        assertTrue(updatedWatcher.isService());
        assertTrue(updatedWatcher.isRepository());
    }

    @Test
    void whenUpdateChaosType_thenChaosTypeIsUpdated() {
        // Given
        Munin newChaosType = new Munin();
        newChaosType.setLevel(50);
        newChaosType.setLatencyActive(true);
        newChaosType.setExceptionActive(true);
        newChaosType.setLatencyRangeStart(100);
        newChaosType.setLatencyRangeEnd(200);

        // When
        Munin updatedChaosType = chaosService.updateChaosType(newChaosType);

        // Then
        assertEquals(50, updatedChaosType.getLevel());
        assertTrue(updatedChaosType.isLatencyActive());
        assertTrue(updatedChaosType.isExceptionActive());
        assertEquals(100, updatedChaosType.getLatencyRangeStart());
        assertEquals(200, updatedChaosType.getLatencyRangeEnd());
    }

    @Test
    void whenUpdateChaosTypeWithInvalidLevel_thenExceptionThrown() {
        // Given
        Munin newChaosType = new Munin();
        newChaosType.setLevel(150); // Invalid level

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            chaosService.updateChaosType(newChaosType)
        );
    }

    @Test
    void whenUpdateChaosTypeWithInvalidLatencyRange_thenExceptionThrown() {
        // Given
        Munin newChaosType = new Munin();
        newChaosType.setLevel(14);
        newChaosType.setLatencyActive(true);
        newChaosType.setLatencyRangeStart(200);
        newChaosType.setLatencyRangeEnd(100); // Invalid range

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            chaosService.updateChaosType(newChaosType)
        );
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
        LokiChaos currentState = chaosService.getCurrentState();

        // Then
        assertTrue(currentState.isEnabled());
        assertTrue(currentState.getWatcher().isRestcontroller());
        assertEquals(50, currentState.getChaosType().getLevel());
    }
} 