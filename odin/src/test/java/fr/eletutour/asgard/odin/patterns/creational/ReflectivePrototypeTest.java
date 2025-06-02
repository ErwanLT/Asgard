package fr.eletutour.asgard.odin.patterns.creational;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectivePrototypeTest {

    static class TestObject {
        private String name;
        private int value;

        public TestObject() {
            this.name = "default";
            this.value = 0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Test
    void testBasicCloning() {
        TestObject prototype = new TestObject();
        ReflectivePrototype<TestObject> reflectivePrototype = new ReflectivePrototype<>(prototype, TestObject.class);

        TestObject clone = reflectivePrototype.clone();

        assertNotNull(clone);
        assertNotSame(prototype, clone);
        assertEquals(prototype.getName(), clone.getName());
        assertEquals(prototype.getValue(), clone.getValue());
    }

    @Test
    void testCustomizedCloning() {
        TestObject prototype = new TestObject();
        ReflectivePrototype<TestObject> reflectivePrototype = new ReflectivePrototype<>(prototype, TestObject.class)
            .customize("name", "custom")
            .customize("value", 42);

        TestObject clone = reflectivePrototype.clone();

        assertEquals("custom", clone.getName());
        assertEquals(42, clone.getValue());
    }

    @Test
    void testResetCustomizations() {
        TestObject prototype = new TestObject();
        ReflectivePrototype<TestObject> reflectivePrototype = new ReflectivePrototype<>(prototype, TestObject.class)
            .customize("name", "custom")
            .reset();

        TestObject clone = reflectivePrototype.clone();

        assertEquals("default", clone.getName());
        assertEquals(0, clone.getValue());
    }

    @Test
    void testNullPrototype() {
        assertThrows(NullPointerException.class, () -> {
            new ReflectivePrototype<>(null, TestObject.class);
        });
    }

    @Test
    void testNullClass() {
        TestObject prototype = new TestObject();
        assertThrows(NullPointerException.class, () -> {
            new ReflectivePrototype<>(prototype, null);
        });
    }

    @Test
    void testInvalidProperty() {
        TestObject prototype = new TestObject();
        ReflectivePrototype<TestObject> reflectivePrototype = new ReflectivePrototype<>(prototype, TestObject.class)
            .customize("invalidProperty", "value");

        assertThrows(RuntimeException.class, () -> {
            reflectivePrototype.clone();
        });
    }
} 