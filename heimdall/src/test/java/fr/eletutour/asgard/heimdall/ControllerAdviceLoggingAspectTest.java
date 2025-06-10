package fr.eletutour.asgard.heimdall;

import fr.eletutour.asgard.heimdall.aspect.ControllerAdviceLoggingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ControllerAdvice
class TestControllerAdvice {
    public String testMethod(String param1, Integer param2) {
        return "result";
    }
}

class ControllerAdviceLoggingAspectTest extends AbstractLoggingAspectTest {

    private ControllerAdviceLoggingAspect aspect;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        super.setUp();
        aspect = new ControllerAdviceLoggingAspect();
        target = new TestControllerAdvice();
        method = TestControllerAdvice.class.getMethod("testMethod", String.class, Integer.class);
    }

    @Test
    void shouldLogEntryAndExit() throws Throwable {
        // Given
        when(joinPoint.proceed()).thenReturn("result");

        // When
        aspect.logControllerAdvice(joinPoint);

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
            aspect.logControllerAdvice(joinPoint);
        } catch (RuntimeException e) {
            // Expected exception
        }
    }
} 