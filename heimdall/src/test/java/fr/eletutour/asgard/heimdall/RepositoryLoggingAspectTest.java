package fr.eletutour.asgard.heimdall;

import fr.eletutour.asgard.heimdall.aspect.RepositoryLoggingAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Repository
class TestRepository {
    public String testMethod(String param1, Integer param2) {
        return "result";
    }
}

class RepositoryLoggingAspectTest extends AbstractLoggingAspectTest {

    private RepositoryLoggingAspect aspect;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        super.setUp();
        aspect = new RepositoryLoggingAspect();
        target = new TestRepository();
        method = TestRepository.class.getMethod("testMethod", String.class, Integer.class);
    }

    @Test
    void shouldLogEntryAndExit() throws Throwable {
        // Given
        when(joinPoint.proceed()).thenReturn("result");

        // When
        aspect.logRepository(joinPoint);

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
            aspect.logRepository(joinPoint);
        } catch (RuntimeException e) {
            // Expected exception
        }
    }
} 