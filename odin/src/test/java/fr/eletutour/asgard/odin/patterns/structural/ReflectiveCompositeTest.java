package fr.eletutour.asgard.odin.patterns.structural;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveCompositeTest {
    private ReflectiveComposite<TestComponent> composite;
    private TestComponent component1;
    private TestComponent component2;
    private TestComponent component3;

    @BeforeEach
    void setUp() {
        composite = new ReflectiveComposite<>(TestComponent.class);
        component1 = new TestComponent("Component1");
        component2 = new TestComponent("Component2");
        component3 = new TestComponent("Component3");
    }

    @Test
    void testAddComponent() {
        composite.add(component1);
        assertEquals(1, composite.getChildren().size());
        assertTrue(composite.getChildren().contains(component1));
    }

    @Test
    void testRemoveComponent() {
        composite.add(component1);
        composite.add(component2);
        composite.remove(component1);
        assertEquals(1, composite.getChildren().size());
        assertFalse(composite.getChildren().contains(component1));
        assertTrue(composite.getChildren().contains(component2));
    }

    @Test
    void testGetChildren() {
        composite.add(component1);
        composite.add(component2);
        composite.add(component3);
        
        var children = composite.getChildren();
        assertEquals(3, children.size());
        assertTrue(children.contains(component1));
        assertTrue(children.contains(component2));
        assertTrue(children.contains(component3));
    }

    @Test
    void testOperation() {
        composite.add(component1);
        composite.add(component2);
        
        // Capture la sortie standard
        var outContent = new java.io.ByteArrayOutputStream();
        var originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));
        
        composite.operation();
        
        // Restaure la sortie standard
        System.setOut(originalOut);
        
        String output = outContent.toString();
        assertTrue(output.contains("Operation on Component1"));
        assertTrue(output.contains("Operation on Component2"));
    }

    @Test
    void testEmptyComposite() {
        assertTrue(composite.getChildren().isEmpty());
    }

    @Test
    void testRemoveNonExistentComponent() {
        composite.add(component1);
        composite.remove(component2);
        assertEquals(1, composite.getChildren().size());
        assertTrue(composite.getChildren().contains(component1));
    }

    // Classe de test interne
    private static class TestComponent {
        private final String name;

        public TestComponent(String name) {
            this.name = name;
        }

        public void operation() {
            System.out.println("Operation on " + name);
        }
    }
} 