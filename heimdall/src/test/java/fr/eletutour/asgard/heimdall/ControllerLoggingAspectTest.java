package fr.eletutour.asgard.heimdall;

import fr.eletutour.asgard.heimdall.aspect.ControllerLoggingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Controller
class TestController {
    public String testMethod(String param1, Integer param2) {
        return "result";
    }
}

class ControllerLoggingAspectTest extends AbstractLoggingAspectTest {

    private ControllerLoggingAspect aspect;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        super.setUp();
        aspect = new ControllerLoggingAspect();
        target = new TestController();
        method = TestController.class.getMethod("testMethod", String.class, Integer.class);
    }

    @Test
    void shouldLogEntryAndExit() throws Throwable {
        // Given
        when(joinPoint.proceed()).thenReturn("result");

        // When
        aspect.logController(joinPoint);

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
            aspect.logController(joinPoint);
        } catch (RuntimeException e) {
            // Expected exception
        }
    }
} 