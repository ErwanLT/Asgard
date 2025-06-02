package fr.eletutour.asgard.odin.patterns.creational;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveSingletonTest {

    static class TestClass {
        private static int instanceCount = 0;
        private final int id;

        public TestClass() {
            this.id = ++instanceCount;
        }

        public int getId() {
            return id;
        }

        public static void resetInstanceCount() {
            instanceCount = 0;
        }
    }

    @BeforeEach
    void setUp() {
        TestClass.resetInstanceCount();
        ReflectiveSingleton.resetAll();
    }

    @Test
    void testGetInstance() {
        TestClass instance1 = ReflectiveSingleton.getInstance(TestClass.class);
        TestClass instance2 = ReflectiveSingleton.getInstance(TestClass.class);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    void testResetInstance() {
        TestClass instance1 = ReflectiveSingleton.getInstance(TestClass.class);
        ReflectiveSingleton.resetInstance(TestClass.class);
        TestClass instance2 = ReflectiveSingleton.getInstance(TestClass.class);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertNotSame(instance1, instance2);
    }

    @Test
    void testResetAll() {
        TestClass instance1 = ReflectiveSingleton.getInstance(TestClass.class);
        ReflectiveSingleton.resetAll();
        TestClass instance2 = ReflectiveSingleton.getInstance(TestClass.class);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertNotSame(instance1, instance2);
    }

    @Test
    void testHasInstance() {
        assertFalse(ReflectiveSingleton.hasInstance(TestClass.class));
        TestClass instance = ReflectiveSingleton.getInstance(TestClass.class);
        assertTrue(ReflectiveSingleton.hasInstance(TestClass.class));
    }

    @Test
    void testMultipleClasses() {
        TestClass instance1 = ReflectiveSingleton.getInstance(TestClass.class);
        String instance2 = ReflectiveSingleton.getInstance(String.class);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertNotSame(instance1, instance2);
    }

    @Test
    void testInstanceCount() {
        ReflectiveSingleton.resetAll();
        TestClass instance1 = ReflectiveSingleton.getInstance(TestClass.class);
        TestClass instance2 = ReflectiveSingleton.getInstance(TestClass.class);
        TestClass instance3 = ReflectiveSingleton.getInstance(TestClass.class);

        assertEquals(1, instance1.getId());
        assertEquals(1, instance2.getId());
        assertEquals(1, instance3.getId());
    }
} 