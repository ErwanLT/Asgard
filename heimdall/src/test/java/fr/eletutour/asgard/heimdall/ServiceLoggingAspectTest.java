package fr.eletutour.asgard.heimdall;

import fr.eletutour.asgard.heimdall.aspect.ServiceLoggingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Service
class TestService {
    public String testMethod(String param1, Integer param2) {
        return "result";
    }
}

class ServiceLoggingAspectTest extends AbstractLoggingAspectTest {

    private ServiceLoggingAspect aspect;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        super.setUp();
        aspect = new ServiceLoggingAspect();
        target = new TestService();
        method = TestService.class.getMethod("testMethod", String.class, Integer.class);
    }

    @Test
    void shouldLogEntryAndExit() throws Throwable {
        // Given
        when(joinPoint.proceed()).thenReturn("result");

        // When
        aspect.logService(joinPoint);

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
            aspect.logService(joinPoint);
        } catch (RuntimeException e) {
            // Expected exception
        }
    }
} 