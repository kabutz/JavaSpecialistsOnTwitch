package eu.javaspecialists.twitch.broadcast1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

abstract class SimpleTreeTest {
    BinaryTree<String> simpleTree;

    @BeforeEach
    void init() {
        simpleTree = create();
    }

    protected abstract BinaryTree<String> create();

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
        int size = 10_000;
        List<String> list =
                IntStream.range(0, size)
                        .mapToObj("str=%010d"::formatted)
                        .collect(Collectors.toList());
        list.forEach(simpleTree::add);
        String search = "str=%010d".formatted(size - 1);
        simpleTree.add(search);
        System.out.println("simpleTree.maxDepth() = " + simpleTree.maxDepth());
        long time = System.nanoTime();
        try {
            for (int i = 0; i < 100_000; i++) {
                assertTrue(simpleTree.contains(search));
            }
        } finally {
            time = System.nanoTime() - time;
            System.out.printf("time = %dms%n", (time / 1_000_000));
        }
    }

    @Test
    void testDepth() {
        int size = 10_000;
        List<String> list =
                IntStream.range(0, size)
                        .mapToObj("str=%010d"::formatted)
                        .collect(Collectors.toList());
        list.forEach(simpleTree::add);
        assertEquals(size, simpleTree.maxDepth());
    }
}