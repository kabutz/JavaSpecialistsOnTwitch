package eu.javaspecialists.twitch.broadcast1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleArrayListTest {
    SimpleArrayList<String> simpleArrayList;

    @BeforeEach
    void init() {
        simpleArrayList = new SimpleArrayList<>();
    }

    @Test
    void testAdd() {
        simpleArrayList.add("Test string");
        assertEquals(1, simpleArrayList.size());
        assertEquals("Test string", simpleArrayList.get(0));
        for (int i = 0; i < 100; i++) {
            simpleArrayList.add("test" + i);
        }
        assertEquals(101, simpleArrayList.size());
        assertEquals("test99", simpleArrayList.get(100));
    }

    @Test
    void testGet() {
        assertThrows(IndexOutOfBoundsException.class, () -> simpleArrayList.get(0));
        simpleArrayList.add("Test string");
        assertEquals("Test string", simpleArrayList.get(0));
    }

    @Test
    void testSize() {
        assertEquals(0, simpleArrayList.size());
        simpleArrayList.add("Test string");
        assertEquals(1, simpleArrayList.size());
    }

    @Test
    void testRemoveAt() {
        assertThrows(IndexOutOfBoundsException.class, () -> simpleArrayList.removeAt(0));
        simpleArrayList.add("Test string");
        simpleArrayList.removeAt(0);
        assertEquals(0, simpleArrayList.size());
        for (int i = 0; i < 100; i++) {
            simpleArrayList.add("test" + i);
        }
        assertEquals(100, simpleArrayList.size());
        while(simpleArrayList.size() != 0)
            simpleArrayList.removeAt(0);
        assertEquals(0, simpleArrayList.size());
    }

    @Test
    void testIterator() {
        simpleArrayList.add("Test string");
        simpleArrayList.add("Test string 2");
        simpleArrayList.add("Test string 3");

        Iterator<String> iterator = simpleArrayList.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("Test string", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Test string 2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Test string 3", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testFastFailIteration() {
        simpleArrayList.add("Test string");
        simpleArrayList.add("Test string 2");
        simpleArrayList.add("Test string 3");

        var iterator = simpleArrayList.iterator();

        // Removal via iterator
        iterator.hasNext();
        iterator.next();
        iterator.remove();
        assertEquals("Test string 2", iterator.next());
        simpleArrayList.add("Test string 4");

        // Modifications outside iterator should lead to fast-fail
        assertThrows(ConcurrentModificationException.class, iterator::next);


        var iterator2 = simpleArrayList.iterator();

        // Removal via iterator
        iterator2.hasNext();
        iterator2.next();
        iterator2.remove();
        assertThrows(IllegalStateException.class, iterator2::remove);
    }

    @Test
    void testFifoQueue() {
        long time = System.nanoTime();
        try {
            for (int i = 0; i < 10_000; i++) {
                simpleArrayList.add("str=" + i);
            }
            for (int i = 0; i < 4_000_000; i++) {
                simpleArrayList.add("str-" + i);
                simpleArrayList.removeAt(0);
            }
            assertEquals(10_000, simpleArrayList.size());
        } finally {
            time = System.nanoTime() - time;
            System.out.printf("time = %dms%n", (time / 1_000_000));
        }
    }
}