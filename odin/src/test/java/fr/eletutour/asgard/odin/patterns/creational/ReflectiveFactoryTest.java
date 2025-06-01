package fr.eletutour.asgard.odin.patterns.creational;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveFactoryTest {
    
    @Test
    void testCreateSimpleObject() {
        ReflectiveFactory<TestInterface> factory = new ReflectiveFactory<>(TestInterface.class);
        TestInterface obj = factory.create("ReflectiveFactoryTest$TestImplementation");
        
        assertNotNull(obj);
        assertInstanceOf(TestImplementation.class, obj);
    }

    @Test
    void testCreateWithParameters() {
        ReflectiveFactory<TestInterface> factory = new ReflectiveFactory<>(TestInterface.class);
        TestInterface obj = factory.create("ReflectiveFactoryTest$TestImplementationWithParams");
        
        assertNotNull(obj);
        assertTrue(obj instanceof TestImplementationWithParams);
    }

    @Test
    void testInvalidClassName() {
        ReflectiveFactory<TestInterface> factory = new ReflectiveFactory<>(TestInterface.class);
        
        assertThrows(RuntimeException.class, () -> {
            factory.create("NonExistentClass");
        });
    }

    @Test
    void testNullClassName() {
        ReflectiveFactory<TestInterface> factory = new ReflectiveFactory<>(TestInterface.class);
        
        assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });
    }

    @Test
    void testInvalidImplementation() {
        ReflectiveFactory<TestInterface> factory = new ReflectiveFactory<>(TestInterface.class);
        
        assertThrows(RuntimeException.class, () -> {
            factory.create("ReflectiveFactoryTest$InvalidImplementation");
        });
    }

    // Interfaces et classes de test
    interface TestInterface {
        String getValue();
    }

    static class TestImplementation implements TestInterface {
        @Override
        public String getValue() {
            return "test";
        }
    }

    static class TestImplementationWithParams implements TestInterface {
        private final String value;

        public TestImplementationWithParams() {
            this.value = "test with params";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    static class InvalidImplementation {
        // Ne respecte pas l'interface
    }
} 