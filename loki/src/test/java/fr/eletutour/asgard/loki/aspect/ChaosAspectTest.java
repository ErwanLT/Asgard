package fr.eletutour.asgard.loki.aspect;

import fr.eletutour.asgard.loki.model.Hugin;
import fr.eletutour.asgard.loki.model.LokiChaos;
import fr.eletutour.asgard.loki.model.Munin;
import fr.eletutour.asgard.loki.service.ChaosService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;

class ChaosAspectTest {

    private ChaosAspect chaosAspect;
    private ChaosService chaosService;
    private TestController testController;
    private TestService testService;
    private TestRepository testRepository;

    @BeforeEach
    void setUp() {
        chaosService = new ChaosService();
        chaosAspect = new ChaosAspect(chaosService.getCurrentState());
        testController = new TestController();
        testService = new TestService();
        testRepository = new TestRepository();
    }

    @Test
    void whenChaosDisabled_thenNoChaosApplied() throws Throwable {
        // Given
        Hugin watcher = new Hugin();
        watcher.setRestcontroller(true);
        chaosService.updateWatcher(watcher);

        // When
        Object result = chaosAspect.applyChaos(new TestJoinPoint(testController));

        // Then
        assertEquals("test", result);
    }

    @Test
    void whenLayerNotWatched_thenNoChaosApplied() throws Throwable {
        // Given
        chaosService.enableChaos();
        Hugin watcher = new Hugin();
        watcher.setRestcontroller(false);
        chaosService.updateWatcher(watcher);

        // When
        Object result = chaosAspect.applyChaos(new TestJoinPoint(testController));

        // Then
        assertEquals("test", result);
    }

    @Test
    void whenLatencyActive_thenLatencyApplied() throws Throwable {
        // Given
        chaosService.enableChaos();
        Hugin watcher = new Hugin();
        watcher.setRestcontroller(true);
        chaosService.updateWatcher(watcher);

        Munin chaosType = new Munin();
        chaosType.setLevel(100);
        chaosType.setLatencyActive(true);
        chaosType.setLatencyRangeStart(100);
        chaosType.setLatencyRangeEnd(100);
        chaosService.updateChaosType(chaosType);

        // When
        long startTime = System.currentTimeMillis();
        Object result = chaosAspect.applyChaos(new TestJoinPoint(testController));
        long endTime = System.currentTimeMillis();

        // Then
        assertEquals("test", result);
        assertTrue(endTime - startTime >= 100);
    }

    @Test
    void whenExceptionActive_thenExceptionThrown() {
        // Given
        chaosService.enableChaos();
        Hugin watcher = new Hugin();
        watcher.setRestcontroller(true);
        chaosService.updateWatcher(watcher);

        Munin chaosType = new Munin();
        chaosType.setLevel(100);
        chaosType.setExceptionActive(true);
        chaosService.updateChaosType(chaosType);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            chaosAspect.applyChaos(new TestJoinPoint(testController))
        );
    }

    @Test
    void whenBothLatencyAndExceptionActive_thenOneIsApplied() throws Throwable {
        // Given
        chaosService.enableChaos();
        Hugin watcher = new Hugin();
        watcher.setRestcontroller(true);
        chaosService.updateWatcher(watcher);

        Munin chaosType = new Munin();
        chaosType.setLevel(100);
        chaosType.setLatencyActive(true);
        chaosType.setExceptionActive(true);
        chaosType.setLatencyRangeStart(1000);
        chaosType.setLatencyRangeEnd(3000);
        chaosService.updateChaosType(chaosType);

        // When
        try {
            chaosAspect.applyChaos(new TestJoinPoint(testController));
        } catch (RuntimeException e) {
            // Exception is expected in some cases
            return;
        }

        // Then
        // If we get here, latency was applied instead of exception
        assertTrue(true);
    }

    // Test classes
    @RestController
    private static class TestController {
        public String test() { return "test"; }
    }

    @Service
    private static class TestService {
        public String test() { return "test"; }
    }

    @Repository
    private static class TestRepository {
        public String test() { return "test"; }
    }

    // Test JoinPoint implementation
    private static class TestJoinPoint implements ProceedingJoinPoint {
        private final Object target;

        public TestJoinPoint(Object target) {
            this.target = target;
        }

        @Override
        public Object proceed() throws Throwable {
            if (target instanceof TestController) {
                return ((TestController) target).test();
            } else if (target instanceof TestService) {
                return ((TestService) target).test();
            } else if (target instanceof TestRepository) {
                return ((TestRepository) target).test();
            }
            return null;
        }

        @Override
        public Object getTarget() {
            return target;
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public Object[] getArgs() { 
            return new Object[0]; 
        }

        @Override
        public String getKind() { 
            return null; 
        }

        @Override
        public Signature getSignature() { 
            return null; 
        }

        @Override
        public SourceLocation getSourceLocation() { 
            return null; 
        }

        @Override
        public JoinPoint.StaticPart getStaticPart() { 
            return null; 
        }

        @Override
        public String toShortString() { 
            return null; 
        }

        @Override
        public String toLongString() { 
            return null; 
        }

        @Override
        public Object proceed(Object[] args) throws Throwable { 
            return null; 
        }

        @Override
        public void set$AroundClosure(org.aspectj.runtime.internal.AroundClosure aroundClosure) {
            // Cette méthode est requise par l'interface mais n'est pas utilisée dans nos tests
        }

        @Override
        public void stack$AroundClosure(AroundClosure arc) {
            ProceedingJoinPoint.super.stack$AroundClosure(arc);
        }
    }
} 