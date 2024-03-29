package eu.javaspecialists.twitch.broadcast1;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

class AVLBinaryTreeTest extends BinaryTreeTest {
    protected BinaryTree<String> create() {
        return new AVLBinaryTree<>();
    }

    @Override
    protected int expectedMaxDepth(int elements) {
        return (int) Math.ceil(Math.log(elements) / Math.log(2));
    }
}