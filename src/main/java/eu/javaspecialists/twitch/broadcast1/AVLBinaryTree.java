package eu.javaspecialists.twitch.broadcast1;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * AVLBinaryTree represents a balanced binary search tree that implements the
 * BinaryTree interface.  It maintains the AVL property, which ensures that the
 * tree is balanced and maintains O(log n) time complexity for insert, remove,
 * and search operations.
 *
 * @param <T> the type of elements stored in the tree, must implement Comparable
 *            interface
 */
// https://www.linkedin.com/video/live/urn:li:ugcPost:7173707499991252992/
public class AVLBinaryTree<T extends Comparable<T>> extends AbstractBinaryTree<T> {
    private Node<T> root;

    @Override
    Node<T> root() {
        return root;
    }

    private static class Node<E extends Comparable<E>>
            implements AbstractBinaryTree.Node<E> {
        private E value;
        private int height;
        private Node<E> left;
        private Node<E> right;

        public Node(E key) {
            this.value = key;
            this.height = 1;  // new nodes are leaf nodes
        }

        @Override
        public Node<E> left() {
            return left;
        }

        @Override
        public Node<E> right() {
            return right;
        }

        @Override
        public E value() {
            return value;
        }

        public void left(Node<E> left) {
            this.left = left;
        }

        public void right(Node<E> right) {
            this.right = right;
        }
    }

    // Get balance factor of a node
    private int getBalance(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    // Get the height of the node
    private int height(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    // Rotate right
    private Node<T> rotateRight(Node<T> node) {
        return rotate(node, Node::left, Node::right, Node::right, Node::left);
    }

    // Rotate left
    private Node<T> rotateLeft(Node<T> node) {
        return rotate(node, Node::right, Node::left, Node::left, Node::right);
    }

    // Rotate
    private Node<T> rotate(Node<T> node, UnaryOperator<Node<T>> newRootGetter,
                           UnaryOperator<Node<T>> newSubtreeGetter,
                           BiConsumer<Node<T>, Node<T>> newRootSetter,
                           BiConsumer<Node<T>, Node<T>> nodeSetter) {
        Node<T> newRoot = newRootGetter.apply(node);
        Node<T> subtree = newSubtreeGetter.apply(newRoot);

        newRootSetter.accept(newRoot, node);
        nodeSetter.accept(node, subtree);

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        newRoot.height = Math.max(height(newRoot.left), height(newRoot.right)) + 1;

        return newRoot;
    }

    // Insert a value into the tree
    public void add(T key) {
        this.root = add(root, key);
        modCount++;
    }

    // Insert a value into a node
    private Node<T> add(Node<T> node, T key) {
        if (node == null) {
            return new Node<>(key);
        }
        int cmp = key.compareTo(node.value);
        if (cmp < 0) {
            node.left = add(node.left, key);
        } else if (cmp > 0) {
            node.right = add(node.right, key);
        } else {
            // Duplicate keys not allowed
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && key.compareTo(node.left.value) < 0) {
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && key.compareTo(node.right.value) > 0) {
            return rotateLeft(node);
        }

        // Left Right Case
        if (balance > 1 && key.compareTo(node.left.value) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Left Case
        if (balance < -1 && key.compareTo(node.right.value) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Remove a value from the tree
    public void remove(T key) {
        this.root = remove(root, key);
        modCount++;
    }

    // Remove a value from a node
    private Node<T> remove(Node<T> node, T key) {
        if (node == null) {
            return node;
        }

        int cmp = key.compareTo(node.value);

        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if ((node.left == null) || (node.right == null)) {
                Node<T> temp = null;
                if (node.left == temp) {
                    temp = node.right;
                } else {
                    temp = node.left;
                }
                if (temp == null) {
                    node = null;
                } else {
                    node = temp;
                }
            } else {

                Node<T> temp = minValueNode(node.right);
                node.value = temp.value;
                node.right = remove(node.right, temp.value);
            }
        }

        if (node == null) {
            return node;
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node<T> minValueNode(Node<T> node) {
        Node<T> current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }
}