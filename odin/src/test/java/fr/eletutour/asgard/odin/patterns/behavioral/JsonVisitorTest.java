package fr.eletutour.asgard.odin.patterns.behavioral;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class JsonVisitorTest {
    
    @Test
    void testSimpleObject() {
        TestObject obj = new TestObject("test", 42);
        JsonVisitor visitor = new JsonVisitor();
        visitor.visit(obj);
        String json = visitor.getJson();
        
        assertTrue(json.contains("\"name\": \"test\""));
        assertTrue(json.contains("\"value\": 42"));
    }

    @Test
    void testNestedObject() {
        TestObject parent = new TestObject("parent", 1);
        TestObject child = new TestObject("child", 2);
        parent.setChild(child);
        
        JsonVisitor visitor = new JsonVisitor();
        visitor.visit(parent);
        String json = visitor.getJson();
        
        assertTrue(json.contains("\"name\": \"parent\""));
        assertTrue(json.contains("\"name\": \"child\""));
    }

    @Test
    void testCollection() {
        List<TestObject> list = Arrays.asList(
            new TestObject("obj1", 1),
            new TestObject("obj2", 2)
        );
        
        JsonVisitor visitor = new JsonVisitor();
        visitor.visit(list);
        String json = visitor.getJson();
        
        assertTrue(json.contains("\"name\": \"obj1\""));
        assertTrue(json.contains("\"name\": \"obj2\""));
    }

    @Test
    void testMap() {
        Map<String, TestObject> map = new HashMap<>();
        map.put("key1", new TestObject("obj1", 1));
        map.put("key2", new TestObject("obj2", 2));
        
        JsonVisitor visitor = new JsonVisitor();
        visitor.visit(map);
        String json = visitor.getJson();
        
        assertTrue(json.contains("\"key1\""));
        assertTrue(json.contains("\"key2\""));
        assertTrue(json.contains("\"name\": \"obj1\""));
        assertTrue(json.contains("\"name\": \"obj2\""));
    }

    @Test
    void testNullValues() {
        TestObject obj = new TestObject(null, 42);
        JsonVisitor visitor = new JsonVisitor();
        visitor.visit(obj);
        String json = visitor.getJson();
        
        assertTrue(json.contains("\"name\": null"));
        assertTrue(json.contains("\"value\": 42"));
    }

    @Test
    void testSpecialCharacters() {
        TestObject obj = new TestObject("test\nwith\"special\\chars", 42);
        JsonVisitor visitor = new JsonVisitor();
        visitor.visit(obj);
        String json = visitor.getJson();
        
        assertTrue(json.contains("test\\nwith\\\"special\\\\chars"));
    }

    // Classe de test
    private static class TestObject {
        private String name;
        private int value;
        private TestObject child;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public void setChild(TestObject child) {
            this.child = child;
        }
    }
} 