package eu.javaspecialists.twitch.broadcast1;

import java.util.*;

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
public class AVLBinaryTree<T extends Comparable<T>> implements BinaryTree<T> {
    private Node<T> root;
    private int modCount = 0;

    private static class Node<E extends Comparable<E>> {
        private E value;
        private int height;
        private Node<E> left;
        private Node<E> right;

        public Node(E key) {
            this.value = key;
            this.height = 1;  // new nodes are leaf nodes
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
        Node<T> newRoot = node.left;
        Node<T> subtree = newRoot.right;

        newRoot.right = node;
        node.left = subtree;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        newRoot.height = Math.max(height(newRoot.left), height(newRoot.right)) + 1;

        return newRoot;
    }

    // Rotate left
    private Node<T> rotateLeft(Node<T> node) {
        Node<T> newRoot = node.right;
        Node<T> subtree = newRoot.left;

        newRoot.left = node;
        node.right = subtree;

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

    @Override
    public boolean contains(T value) {
        return contains(root, value);
    }

    private boolean contains(Node<T> current, T value) {
        if (current == null) {
            return false;
        }
        if (value.compareTo(current.value) == 0) {
            return true;
        }
        return value.compareTo(current.value) < 0
                ? contains(current.left, value)
                : contains(current.right, value);
    }


    @Override
    public Iterator<T> iterator() {
        return new AVLTreeIterator();
    }

    private class AVLTreeIterator implements Iterator<T> {
        private final int initialModCount = modCount;
        private final Deque<Node<T>> nodeStack = new ArrayDeque<>();

        {
            if (root != null) {
                pushLeftSubtree(root);
            }
        }

        private void pushLeftSubtree(Node<T> node) {
            while (node != null) {
                nodeStack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !nodeStack.isEmpty();
        }

        @Override
        public T next() {
            checkForComodification();
            if (!hasNext()) throw new NoSuchElementException();

            Node<T> nextNode = nodeStack.pop();
            pushLeftSubtree(nextNode.right);

            return nextNode.value;
        }

        private void checkForComodification() {
            if (modCount != initialModCount)
                throw new ConcurrentModificationException();
        }
    }

    @Override
    public int size() {
        int size = 0;
        for (T t : this) {
            size++;
        }
        return size;
    }

    /**
     * Method to measure the maxDepth of the tree.
     */
    @Override
    public int maxDepth() {
        if (root == null) {
            return 0;
        }

        var deque = new ArrayDeque<Pair<Node<T>, Integer>>();
        deque.push(new Pair<>(root, 1));
        int maxDepth = 0;

        while (!deque.isEmpty()) {
            Pair<Node<T>, Integer> current = deque.pop();
            Node<T> node = current.first;
            int currentDepth = current.second;

            if (node != null) {
                maxDepth = Math.max(maxDepth, currentDepth);
                if (node.right != null) {
                    deque.push(new Pair<>(node.right, currentDepth + 1));
                }
                if (node.left != null) {
                    deque.push(new Pair<>(node.left, currentDepth + 1));
                }
            }
        }

        return maxDepth;
    }

    private record Pair<U, V>(U first, V second) {
    }

    public static void main(String... args) {
        var tree = new AVLBinaryTree<Integer>();
        for (int i = 0; i < 10; i++) {
            tree.add(i);
        }
        for (Integer i : tree) {
            System.out.println(i);
        }
    }
}