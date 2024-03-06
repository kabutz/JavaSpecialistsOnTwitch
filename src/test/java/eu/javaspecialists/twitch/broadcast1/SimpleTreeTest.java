package eu.javaspecialists.twitch.broadcast1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTreeTest {
    SimpleUnbalancedBinaryTree<String> simpleTree;

    @BeforeEach
    void init() {
        simpleTree = create();
    }

    protected SimpleUnbalancedBinaryTree<String> create() {
        return new SimpleUnbalancedBinaryTree<>();
    }

    // @Test
    void testUnbalanced() {
        // IntStream.range(0, 10)
        ThreadLocalRandom.current().ints(10, 0, 100)
                .mapToObj(i -> "s" + i)
                .forEach(simpleTree::add);
        for (String s : simpleTree) {
            System.out.println(s);
        }
    }

    @Test
    void testAdd() {
        simpleTree.add("Test string");
        assertEquals(1, simpleTree.size());
        assertTrue(simpleTree.contains("Test string"));
        for (int i = 0; i < 100; i++) {
            simpleTree.add("test" + i);
        }
        assertEquals(101, simpleTree.size());
        assertTrue(simpleTree.contains("test99"));
    }

    @Test
    void testGet() {
        simpleTree.add("Test string");
        assertTrue(simpleTree.contains("Test string"));
    }

    @Test
    void testSize() {
        assertEquals(0, simpleTree.size());
        simpleTree.add("Test string");
        assertEquals(1, simpleTree.size());
    }

    @Test
    void testRemove() {
        simpleTree.remove("not there");
        simpleTree.add("Test string");
        simpleTree.remove("Test string");
        assertEquals(0, simpleTree.size());
        for (int i = 0; i < 100; i++) {
            simpleTree.add("test" + i);
        }
        assertEquals(100, simpleTree.size());
        Iterator<String> it;
        while ((it = simpleTree.iterator()).hasNext()) {
            simpleTree.remove(it.next());
        }
        assertEquals(0, simpleTree.size());
    }

    @Test
    void testIterator() {
        simpleTree.add("Test string");
        simpleTree.add("Test string 2");
        simpleTree.add("Test string 3");

        Iterator<String> iterator = simpleTree.iterator();
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
        simpleTree.add("Test string");
        simpleTree.add("Test string 2");
        simpleTree.add("Test string 3");

        var iterator = simpleTree.iterator();

        // Removal via iterator
        iterator.hasNext();
        iterator.next();
        simpleTree.add("Test string 4");

        // Modifications outside iterator should lead to fast-fail
        assertThrows(ConcurrentModificationException.class, iterator::next);


        var iterator2 = simpleTree.iterator();

    }

    @Test
    void testUnbalancedContains() {
        int size = 1_000_000;
        List<String> list =
                // ThreadLocalRandom.current()
                // .ints(10_000_000, 0, 1_000_000_000)
                IntStream.range(0, size)
                        .mapToObj(i -> "str=" + i)
                        .toList();
        list.forEach(simpleTree::add);
        String search = "str=" + (size - 1);
        simpleTree.add(search);
        long time = System.nanoTime();
        try {
            for (int i = 0; i < 10_000_000; i++) {
                assertTrue(simpleTree.contains(search));
            }
        } finally {
            time = System.nanoTime() - time;
            System.out.printf("time = %dms%n", (time / 1_000_000));
        }
    }
}