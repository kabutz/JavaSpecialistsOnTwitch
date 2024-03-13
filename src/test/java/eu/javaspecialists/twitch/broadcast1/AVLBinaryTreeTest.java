package eu.javaspecialists.twitch.broadcast1;

class AVLBinaryTreeTest extends BinaryTreeTest {
    protected BinaryTree<String> create() {
        return new AVLBinaryTree<>();
    }

    @Override
    protected int expectedMaxDepth(int elements) {
        return (int) Math.ceil(Math.log(elements) / Math.log(2));
    }
}