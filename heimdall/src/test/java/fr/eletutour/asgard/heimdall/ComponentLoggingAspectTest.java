package fr.eletutour.asgard.heimdall;

import fr.eletutour.asgard.heimdall.aspect.ComponentLoggingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Component
class TestComponent {
    public String testMethod(String param1, Integer param2) {
        return "result";
    }
}

class ComponentLoggingAspectTest extends AbstractLoggingAspectTest {

    private ComponentLoggingAspect aspect;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        super.setUp();
        aspect = new ComponentLoggingAspect();
        target = new TestComponent();
        method = TestComponent.class.getMethod("testMethod", String.class, Integer.class);
    }

    @Test
    void shouldLogEntryAndExit() throws Throwable {
        // Given
        when(joinPoint.proceed()).thenReturn("result");

        // When
        aspect.logComponent(joinPoint);

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
            aspect.logComponent(joinPoint);
        } catch (RuntimeException e) {
            // Expected exception
        }
    }
} 