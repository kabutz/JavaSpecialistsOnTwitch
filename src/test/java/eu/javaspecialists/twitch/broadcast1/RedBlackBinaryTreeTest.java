package eu.javaspecialists.twitch.broadcast1;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class RedBlackBinaryTreeTest extends BinaryTreeTest {
    protected BinaryTree<String> create() {
        return new RedBlackBinaryTree<>();
    }

    @Override
    protected int expectedMaxDepth(int elements) {
        return (int) Math.ceil(Math.log(elements) / Math.log(2) * 2);
    }
}