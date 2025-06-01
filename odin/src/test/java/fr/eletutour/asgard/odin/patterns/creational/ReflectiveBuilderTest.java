package fr.eletutour.asgard.odin.patterns.creational;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveBuilderTest {
    
    @Test
    void testBuildSimpleObject() {
        ReflectiveBuilder<TestObject> builder = new ReflectiveBuilder<>(TestObject.class);
        TestObject obj = builder
            .with("name", "Test")
            .with("value", 42)
            .build();
        
        assertNotNull(obj);
        assertEquals("Test", obj.getName());
        assertEquals(42, obj.getValue());
    }

    @Test
    void testBuildWithNestedObject() {
        ReflectiveBuilder<ComplexObject> builder = new ReflectiveBuilder<>(ComplexObject.class);
        ComplexObject obj = builder
            .with("name", "Complex")
            .with("nested", new TestObject("Nested", 100))
            .build();
        
        assertNotNull(obj);
        assertEquals("Complex", obj.getName());
        assertNotNull(obj.getNested());
        assertEquals("Nested", obj.getNested().getName());
        assertEquals(100, obj.getNested().getValue());
    }

    @Test
    void testBuildWithInvalidProperty() {
        ReflectiveBuilder<TestObject> builder = new ReflectiveBuilder<>(TestObject.class);
        
        assertThrows(RuntimeException.class, () -> {
            builder.with("nonExistentProperty", "value");
        });
    }

    @Test
    void testBuildWithInvalidValueType() {
        ReflectiveBuilder<TestObject> builder = new ReflectiveBuilder<>(TestObject.class);
        
        assertThrows(RuntimeException.class, () -> {
            builder
                .with("name", "Test")
                .with("value", "notAnInteger")
                .build();
        });
    }

    @Test
    void testBuildWithNullValue() {
        ReflectiveBuilder<TestObject> builder = new ReflectiveBuilder<>(TestObject.class);
        TestObject obj = builder
            .with("name", null)
            .with("value", 42)
            .build();
        
        assertNotNull(obj);
        assertNull(obj.getName());
        assertEquals(42, obj.getValue());
    }

    // Classes de test
    static class TestObject {
        private String name;
        private int value;

        public TestObject() {}

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    static class ComplexObject {
        private String name;
        private TestObject nested;

        public ComplexObject() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TestObject getNested() { return nested; }
        public void setNested(TestObject nested) { this.nested = nested; }
    }
} 