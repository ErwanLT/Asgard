package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.config.MimirProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class UmlDiagramServiceTest {

    @TempDir
    Path tempDir;

    private UmlDiagramService umlDiagramService;
    private MimirProperties properties;

    @BeforeEach
    void setUp() {
        properties = new MimirProperties();
        properties.getDocumentation().setOutputDir(tempDir.toString());
        umlDiagramService = new UmlDiagramService(properties);
    }

    @Test
    void shouldGenerateClassDiagram() throws IOException {
        // Given
        class TestClass {
            private String field1;
            protected int field2;
            public boolean field3;
            
            public void method1() {}
            private String method2(int param) { return null; }
        }

        // When
        umlDiagramService.generateClassDiagram(TestClass.class);

        // Then
        Path diagramFile = tempDir.resolve("diagrams/testclass_diagram.png");
        assertThat(diagramFile).exists();
        assertThat(Files.size(diagramFile)).isGreaterThan(0);
    }

    @Test
    void shouldGenerateDiagramWithInheritance() throws IOException {
        // Given
        class ParentClass {
            public void parentMethod() {}
        }
        
        class ChildClass extends ParentClass {
            private String childField;
            public void childMethod() {}
        }

        // When
        umlDiagramService.generateClassDiagram(ChildClass.class);

        // Then
        Path diagramFile = tempDir.resolve("diagrams/childclass_diagram.png");
        assertThat(diagramFile).exists();
        assertThat(Files.size(diagramFile)).isGreaterThan(0);
    }

    @Test
    void shouldGenerateDiagramWithInterfaces() throws IOException {
        // Given
        interface TestInterface {
            void interfaceMethod();
        }
        
        class TestClass implements TestInterface {
            public void interfaceMethod() {}
        }

        // When
        umlDiagramService.generateClassDiagram(TestClass.class);

        // Then
        Path diagramFile = tempDir.resolve("diagrams/testclass_diagram.png");
        assertThat(diagramFile).exists();
        assertThat(Files.size(diagramFile)).isGreaterThan(0);
    }

    @Test
    void shouldGenerateDiagramWithAssociations() throws IOException {
        // Given
        class AssociatedClass {
            private String field;
        }
        
        class TestClass {
            private AssociatedClass association;
            private String simpleField;
        }

        // When
        umlDiagramService.generateClassDiagram(TestClass.class);

        // Then
        Path diagramFile = tempDir.resolve("diagrams/testclass_diagram.png");
        assertThat(diagramFile).exists();
        assertThat(Files.size(diagramFile)).isGreaterThan(0);
    }
} 