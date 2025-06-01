package fr.eletutour.asgard.odin.patterns.structural;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveDecoratorTest {
    
    // Interface de base
    interface Service {
        String getData(String id);
        int calculate(int a, int b);
    }

    // Implémentation de base
    static class ServiceImpl implements Service {
        @Override
        public String getData(String id) {
            return "Data for " + id;
        }

        @Override
        public int calculate(int a, int b) {
            return a + b;
        }
    }

    @Test
    void testBasicDecoration() {
        Service service = new ServiceImpl();
        Service decoratedService = new ReflectiveDecorator<>(service, Service.class)
            .build();

        assertEquals("Data for 123", decoratedService.getData("123"));
        assertEquals(5, decoratedService.calculate(2, 3));
    }

    @Test
    void testLoggingDecoration() {
        Service service = new ServiceImpl();
        
        // Capture la sortie standard
        var outContent = new java.io.ByteArrayOutputStream();
        var originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));
        
        Service decoratedService = new ReflectiveDecorator<>(service, Service.class)
            .logBefore("getData")
            .build();

        decoratedService.getData("123");
        
        // Restaure la sortie standard
        System.setOut(originalOut);
        
        String output = outContent.toString();
        assertTrue(output.contains("Avant l'appel de getData"));
        assertTrue(output.contains("Après l'appel de getData"));
    }

    @Test
    void testCachingDecoration() {
        Service service = new ServiceImpl();
        Service decoratedService = new ReflectiveDecorator<>(service, Service.class)
            .cache("getData")
            .build();

        // Premier appel
        String result1 = decoratedService.getData("123");
        // Deuxième appel avec les mêmes paramètres
        String result2 = decoratedService.getData("123");

        assertEquals(result1, result2);
    }

    @Test
    void testRetryDecoration() {
        // Service qui échoue deux fois avant de réussir
        class FailingService implements Service {
            private int attempts = 0;

            @Override
            public String getData(String id) {
                if (attempts++ < 2) {
                    throw new RuntimeException("Temporary failure");
                }
                return "Data for " + id;
            }

            @Override
            public int calculate(int a, int b) {
                return a + b;
            }
        }

        Service service = new FailingService();
        Service decoratedService = new ReflectiveDecorator<>(service, Service.class)
            .retry("getData", 3)
            .build();

        String result = decoratedService.getData("123");
        assertEquals("123", result);
    }

    @Test
    void testMultipleDecorations() {
        Service service = new ServiceImpl();
        
        // Capture la sortie standard
        var outContent = new java.io.ByteArrayOutputStream();
        var originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));
        
        Service decoratedService = new ReflectiveDecorator<>(service, Service.class)
            .cache("getData")
            .logBefore("getData")
            .retry("getData", 3)
            .build();

        // Premier appel
        String result1 = decoratedService.getData("123");
        // Deuxième appel avec les mêmes paramètres
        String result2 = decoratedService.getData("123");
        
        // Restaure la sortie standard
        System.setOut(originalOut);
        
        // Vérifie que les résultats sont identiques (cache)
        assertEquals(result1, result2);
        
        // Vérifie que le message de log n'apparaît qu'une seule fois (cache)
        String output = outContent.toString();
        int logCount = countOccurrences(output, "Avant l'appel de getData");
        assertEquals(2, logCount, "Le message de log devrait apparaître une seule fois à cause du cache");
    }

    @Test
    void testNullComponent() {
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveDecorator<>(null, Service.class);
        });
    }

    @Test
    void testNullInterface() {
        Service service = new ServiceImpl();
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveDecorator<>(service, null);
        });
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
} 