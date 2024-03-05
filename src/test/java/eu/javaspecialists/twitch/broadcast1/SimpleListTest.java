package eu.javaspecialists.twitch.broadcast1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

abstract class SimpleListTest {
    SimpleList<String> simpleList;

    @BeforeEach
    void init() {
        simpleList = create();
    }

    protected abstract SimpleList<String> create();

    @Test
    void testAdd() {
        simpleList.add("Test string");
        assertEquals(1, simpleList.size());
        assertEquals("Test string", simpleList.get(0));
        for (int i = 0; i < 100; i++) {
            simpleList.add("test" + i);
        }
        assertEquals(101, simpleList.size());
        assertEquals("test99", simpleList.get(100));
    }

    @Test
    void testGet() {
        assertThrows(IndexOutOfBoundsException.class, () -> simpleList.get(0));
        simpleList.add("Test string");
        assertEquals("Test string", simpleList.get(0));
    }

    @Test
    void testSize() {
        assertEquals(0, simpleList.size());
        simpleList.add("Test string");
        assertEquals(1, simpleList.size());
    }

    @Test
    void testRemoveAt() {
        assertThrows(IndexOutOfBoundsException.class, () -> simpleList.removeAt(0));
        simpleList.add("Test string");
        simpleList.removeAt(0);
        assertEquals(0, simpleList.size());
        for (int i = 0; i < 100; i++) {
            simpleList.add("test" + i);
        }
        assertEquals(100, simpleList.size());
        while(simpleList.size() != 0)
            simpleList.removeAt(0);
        assertEquals(0, simpleList.size());
    }

    @Test
    void testIterator() {
        simpleList.add("Test string");
        simpleList.add("Test string 2");
        simpleList.add("Test string 3");

        Iterator<String> iterator = simpleList.iterator();
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
        simpleList.add("Test string");
        simpleList.add("Test string 2");
        simpleList.add("Test string 3");

        var iterator = simpleList.iterator();

        // Removal via iterator
        iterator.hasNext();
        iterator.next();
        iterator.remove();
        assertEquals("Test string 2", iterator.next());
        simpleList.add("Test string 4");

        // Modifications outside iterator should lead to fast-fail
        assertThrows(ConcurrentModificationException.class, iterator::next);


        var iterator2 = simpleList.iterator();

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
                simpleList.add("str=" + i);
            }
            for (int i = 0; i < 4_000_000; i++) {
                simpleList.add("str-" + i);
                simpleList.removeAt(0);
            }
            assertEquals(10_000, simpleList.size());
        } finally {
            time = System.nanoTime() - time;
            System.out.printf("time = %dms%n", (time / 1_000_000));
        }
    }
}