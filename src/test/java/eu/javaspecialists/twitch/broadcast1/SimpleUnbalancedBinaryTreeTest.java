package eu.javaspecialists.twitch.broadcast1;

class SimpleUnbalancedBinaryTreeTest extends BinaryTreeTest {
    protected BinaryTree<String> create() {
        return new SimpleUnbalancedBinaryTree<>();
    }

    @Override
    protected int expectedMaxDepth(int elements) {
        return elements;
    }
}