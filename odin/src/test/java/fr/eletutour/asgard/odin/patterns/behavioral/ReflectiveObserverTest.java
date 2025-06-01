package fr.eletutour.asgard.odin.patterns.behavioral;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveObserverTest {
    
    @Test
    void testSimpleUpdate() {
        TestSubject subject = new TestSubject();
        ReflectiveObserver<String> observer = new ReflectiveObserver<>(subject, "onUpdate");
        
        observer.update("test");
        assertEquals("test", subject.getLastUpdate());
    }

    @Test
    void testMultipleUpdates() {
        TestSubject subject = new TestSubject();
        ReflectiveObserver<String> observer = new ReflectiveObserver<>(subject, "onUpdate");
        
        observer.update("test1");
        observer.update("test2");
        assertEquals("test2", subject.getLastUpdate());
    }

    @Test
    void testDifferentMethodName() {
        TestSubject subject = new TestSubject();
        ReflectiveObserver<String> observer = new ReflectiveObserver<>(subject, "handleUpdate");
        
        observer.update("test");
        assertEquals("test", subject.getLastHandledUpdate());
    }

    @Test
    void testInvalidMethodName() {
        TestSubject subject = new TestSubject();
        
        assertThrows(RuntimeException.class, () -> {
            ReflectiveObserver<String> observer = new ReflectiveObserver<>(subject, "nonExistentMethod");
            observer.update("test");
        });
    }

    @Test
    void testNullTarget() {
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveObserver<>(null, "onUpdate");
        });
    }

    @Test
    void testNullMethodName() {
        TestSubject subject = new TestSubject();
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveObserver<>(subject, null);
        });
    }

    @Test
    void testMethodWithDifferentParameterType() {
        TestSubject subject = new TestSubject();
        ReflectiveObserver<Integer> observer = new ReflectiveObserver<>(subject, "onNumberUpdate");
        
        observer.update(42);
        assertEquals(42, subject.getLastNumberUpdate());
    }

    // Classe de test
    private static class TestSubject {
        private String lastUpdate;
        private String lastHandledUpdate;
        private int lastNumberUpdate;

        public void onUpdate(String update) {
            this.lastUpdate = update;
        }

        public void handleUpdate(String update) {
            this.lastHandledUpdate = update;
        }

        public void onNumberUpdate(int number) {
            this.lastNumberUpdate = number;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public String getLastHandledUpdate() {
            return lastHandledUpdate;
        }

        public int getLastNumberUpdate() {
            return lastNumberUpdate;
        }
    }
} 