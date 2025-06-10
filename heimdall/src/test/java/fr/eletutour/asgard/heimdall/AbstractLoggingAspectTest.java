package fr.eletutour.asgard.heimdall;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

public abstract class AbstractLoggingAspectTest {
    
    @Mock
    protected ProceedingJoinPoint joinPoint;
    
    @Mock
    protected MethodSignature methodSignature;
    
    @Mock
    protected Signature signature;
    
    protected Object target;
    protected Method method;
    protected String methodName;
    protected String className;
    protected Object[] args;
    
    @BeforeEach
    void setUp() throws NoSuchMethodException {
        MockitoAnnotations.openMocks(this);
        
        // Configuration des mocks communs
        target = new TestTarget();
        method = TestTarget.class.getMethod("testMethod", String.class, Integer.class);
        methodName = method.getName();
        className = target.getClass().getSimpleName();
        args = new Object[]{"test", 123};
        
        // Configuration des mocks pour la signature
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(methodSignature.getName()).thenReturn(methodName);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
    }
    
    // Classe de test pour les mocks
    public static class TestTarget {
        public String testMethod(String param1, Integer param2) {
            return "result";
        }
    }
} 