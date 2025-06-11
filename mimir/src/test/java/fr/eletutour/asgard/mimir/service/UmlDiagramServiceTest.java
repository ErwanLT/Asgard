package fr.eletutour.asgard.mimir.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UmlDiagramServiceTest {

    private UmlDiagramService umlDiagramService;

    @BeforeEach
    void setUp() {
        umlDiagramService = new UmlDiagramService();
    }

    @Test
    void generateClassDiagram_ShouldGenerateValidMermaidDiagram() {
        // Arrange
        class TestClass {
            private String privateField;
            protected int protectedField;
            public boolean publicField;

            private void privateMethod() {}
            protected void protectedMethod() {}
            public void publicMethod() {}
        }

        // Act
        String diagram = umlDiagramService.generateClassDiagram(TestClass.class);

        // Assert
        assertNotNull(diagram);
        assertTrue(diagram.contains("classDiagram"));
        assertTrue(diagram.contains("class TestClass"));
        assertTrue(diagram.contains("-String privateField"));
        assertTrue(diagram.contains("#int protectedField"));
        assertTrue(diagram.contains("+boolean publicField"));
        assertTrue(diagram.contains("-void privateMethod()"));
        assertTrue(diagram.contains("#void protectedMethod()"));
        assertTrue(diagram.contains("+void publicMethod()"));
    }

    @Test
    void generateClassDiagram_ShouldHandleInheritance() {
        // Arrange
        class ParentClass {}
        class ChildClass extends ParentClass {}

        // Act
        String diagram = umlDiagramService.generateClassDiagram(ChildClass.class);

        // Assert
        assertNotNull(diagram);
        assertTrue(diagram.contains("ChildClass --|> ParentClass"));
    }

    @Test
    void generateClassDiagram_ShouldHandleInterfaces() {
        // Arrange
        interface TestInterface {}
        class TestClass implements TestInterface {}

        // Act
        String diagram = umlDiagramService.generateClassDiagram(TestClass.class);

        // Assert
        assertNotNull(diagram);
        assertTrue(diagram.contains("TestClass ..|> TestInterface"));
    }

    @Test
    void generateClassDiagram_ShouldHandleAssociations() {
        // Arrange
        class AssociatedClass {}
        class TestClass {
            private AssociatedClass associatedField;
        }

        // Act
        String diagram = umlDiagramService.generateClassDiagram(TestClass.class);

        // Assert
        assertNotNull(diagram);
        assertTrue(diagram.contains("TestClass --> AssociatedClass"));
    }

    @Test
    void generateClassDiagram_ShouldHandleMethodParameters() {
        // Arrange
        class TestClass {
            public void testMethod(String param1, int param2) {}
        }

        // Act
        String diagram = umlDiagramService.generateClassDiagram(TestClass.class);

        // Assert
        assertNotNull(diagram);
        assertTrue(diagram.contains("+void testMethod(String param1, int param2)"));
    }
} 