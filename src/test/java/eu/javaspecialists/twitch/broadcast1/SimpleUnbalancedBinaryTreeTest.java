package eu.javaspecialists.twitch.broadcast1;

class SimpleUnbalancedBinaryTreeTest extends SimpleTreeTest {
    protected BinaryTree<String> create() {
        return new SimpleUnbalancedBinaryTree<>();
    }
}