package fr.eletutour.asgard.odin.patterns.structural;

import org.junit.jupiter.api.Test;

import java.lang.reflect.UndeclaredThrowableException;

import static org.junit.jupiter.api.Assertions.*;

class ReflectiveAdapterTest {
    
    // Interface cible
    interface Target {
        String process(String input);
        int calculate(int a, int b);
    }

    // Classe à adapter
    static class Adaptee {
        public String transform(String input) {
            return input.toUpperCase();
        }

        public int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    void testDirectMethodMapping() {
        Adaptee adaptee = new Adaptee();
        ReflectiveAdapter<Target> adapter = new ReflectiveAdapter<>(adaptee, Target.class);
        Target target = adapter.adapt();

        assertEquals("HELLO", target.process("hello"));
    }

    @Test
    void testCompatibleMethodMapping() {
        Adaptee adaptee = new Adaptee();
        ReflectiveAdapter<Target> adapter = new ReflectiveAdapter<>(adaptee, Target.class);
        Target target = adapter.adapt();

        assertEquals(5, target.calculate(2, 3));
    }

    @Test
    void testUnsupportedMethod() {
        Adaptee adaptee = new Adaptee();
        ReflectiveAdapter<Target> adapter = new ReflectiveAdapter<>(adaptee, Target.class);
        Target target = adapter.adapt();

        assertThrows(UndeclaredThrowableException.class, () -> {
            // Méthode qui n'existe pas dans l'adapté
            target.process(null);
        });
    }

    @Test
    void testNullAdaptee() {
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveAdapter<>(null, Target.class);
        });
    }

    @Test
    void testNullInterface() {
        Adaptee adaptee = new Adaptee();
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveAdapter<>(adaptee, null);
        });
    }

    @Test
    void testMethodWithDifferentName() {
        // Interface avec une méthode qui a un nom différent
        interface DifferentTarget {
            String convert(String input);
        }

        Adaptee adaptee = new Adaptee();
        ReflectiveAdapter<DifferentTarget> adapter = new ReflectiveAdapter<>(adaptee, DifferentTarget.class);
        DifferentTarget target = adapter.adapt();

        assertEquals("HELLO", target.convert("hello"));
    }
} 