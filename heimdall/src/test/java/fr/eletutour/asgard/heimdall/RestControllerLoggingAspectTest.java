package fr.eletutour.asgard.heimdall;

import fr.eletutour.asgard.heimdall.aspect.RestControllerLoggingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RestController
class TestRestController {
    public String testMethod(String param1, Integer param2) {
        return "result";
    }
}

class RestControllerLoggingAspectTest extends AbstractLoggingAspectTest {

    private RestControllerLoggingAspect aspect;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        super.setUp();
        aspect = new RestControllerLoggingAspect();
        target = new TestRestController();
        method = TestRestController.class.getMethod("testMethod", String.class, Integer.class);
    }

    @Test
    void shouldLogEntryAndExit() throws Throwable {
        // Given
        when(joinPoint.proceed()).thenReturn("result");

        // When
        aspect.logRestController(joinPoint);

        // Then
        verify(joinPoint).proceed();
    }

    @Test
    void shouldLogError() throws Throwable {
        // Given
        RuntimeException exception = new RuntimeException("Test error");
        when(joinPoint.proceed()).thenThrow(exception);

        // When/Then
        try {
            aspect.logRestController(joinPoint);
        } catch (RuntimeException e) {
            // Expected exception
        }
    }
} 