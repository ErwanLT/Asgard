package fr.eletutour.asgard.odin.patterns.creational;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectiveObjectPoolTest {

    static class TestObject {
        private boolean used;

        public TestObject() {
            this.used = false;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }
    }

    @Test
    void testBasicPool() {
        ReflectiveObjectPool<TestObject> pool = new ReflectiveObjectPool<>(TestObject.class, 2, null);

        TestObject obj1 = pool.borrow();
        TestObject obj2 = pool.borrow();

        assertNotNull(obj1);
        assertNotNull(obj2);
        assertNotSame(obj1, obj2);
        assertEquals(2, pool.getCreatedCount());
        assertEquals(2, pool.getBorrowedCount());
        assertEquals(0, pool.getAvailableCount());
    }

    @Test
    void testRelease() {
        ReflectiveObjectPool<TestObject> pool = new ReflectiveObjectPool<>(TestObject.class, 2, null);

        TestObject obj1 = pool.borrow();
        pool.release(obj1);

        assertEquals(1, pool.getCreatedCount());
        assertEquals(0, pool.getBorrowedCount());
        assertEquals(1, pool.getAvailableCount());
    }

    @Test
    void testResetFunction() {
        ReflectiveObjectPool<TestObject> pool = new ReflectiveObjectPool<>(
            TestObject.class,
            2,
            obj -> obj.setUsed(false)
        );

        TestObject obj = pool.borrow();
        obj.setUsed(true);
        pool.release(obj);
        TestObject reusedObj = pool.borrow();

        assertFalse(reusedObj.isUsed());
    }

    @Test
    void testMaxSize() {
        ReflectiveObjectPool<TestObject> pool = new ReflectiveObjectPool<>(TestObject.class, 2, null);

        TestObject obj1 = pool.borrow();
        TestObject obj2 = pool.borrow();
        TestObject obj3 = pool.borrow();

        assertNotNull(obj1);
        assertNotNull(obj2);
        assertNull(obj3);
        assertEquals(2, pool.getCreatedCount());
        assertEquals(2, pool.getBorrowedCount());
    }

    @Test
    void testNullClass() {
        assertThrows(NullPointerException.class, () -> {
            new ReflectiveObjectPool<>(null, 2, null);
        });
    }

    @Test
    void testInvalidMaxSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ReflectiveObjectPool<>(TestObject.class, 0, null);
        });
    }

    @Test
    void testIsEmpty() {
        ReflectiveObjectPool<TestObject> pool = new ReflectiveObjectPool<>(TestObject.class, 2, null);
        assertTrue(pool.isEmpty());

        TestObject obj = pool.borrow();
        assertTrue(pool.isEmpty());

        pool.release(obj);
        assertFalse(pool.isEmpty());
    }

    @Test
    void testIsFull() {
        ReflectiveObjectPool<TestObject> pool = new ReflectiveObjectPool<>(TestObject.class, 2, null);
        assertFalse(pool.isFull());

        TestObject obj1 = pool.borrow();
        TestObject obj2 = pool.borrow();
        assertTrue(pool.isFull());

        pool.release(obj1);
        assertTrue(pool.isFull());
    }
} 