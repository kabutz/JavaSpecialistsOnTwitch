package eu.javaspecialists.twitch.broadcast1;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A simple unbalanced binary tree implementation.
 *
 * @param <T> the type of elements in the tree, must implement the
 *            Comparable interface
 */
// see https://www.linkedin.com/video/live/urn:li:ugcPost:7171037281166835712/
// and https://www.linkedin.com/video/live/urn:li:ugcPost:7171047499154079745/
public class SimpleUnbalancedBinaryTree<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> root;
    private int modCount = 0;

    public void add(T value) {
        modCount++;
        if (root == null) {
            root = new Node<>(value);
        } else {
            add(root, value);
        }
    }

    private void add(Node<T> current, T value) {
        if (value.compareTo(current.value) < 0) {
            if (current.left == null) {
                current.left = new Node<>(value);
            } else {
                add(current.left, value);
            }
        } else if (value.compareTo(current.value) > 0) {
            if (current.right == null) {
                current.right = new Node<>(value);
            } else {
                add(current.right, value);
            }
        }
    }

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

    public void remove(T value) {
        root = remove(root, value);
    }

    private Node<T> remove(Node<T> current, T value) {
        if (current == null) {
            return null;
        }

        if (value.compareTo(current.value) < 0) {
            current.left = remove(current.left, value);
        } else if (value.compareTo(current.value) > 0) {
            current.right = remove(current.right, value);
        } else {
            // value is the same as current.value
            if (current.left == null && current.right == null) {
                return null;
            } else if (current.right == null) {
                return current.left;
            } else if (current.left == null) {
                return current.right;
            } else {
                // Node with two children, finds the minimum in the right subtree,
                // replace current.value by that minimum and delete that minimum
                T minValue = findMinValue(current.right);
                current.value = minValue;
                current.right = remove(current.right, minValue);
                modCount++;
            }
        }

        return current;
    }

    private T findMinValue(Node<T> node) {
        return node.left == null ? node.value : findMinValue(node.left);
    }

    private static class Node<T extends Comparable<T>> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
            left = null;
            right = null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final int expectedModCount = modCount;
            private final Queue<Node<T>> nodes = new LinkedList<>() {{
                if (root != null) {
                    add(root);
                }
            }};

            @Override
            public boolean hasNext() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                return !nodes.isEmpty();
            }

            @Override
            public T next() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                Node<T> node = nodes.poll();
                if (node.left != null) nodes.add(node.left);
                if (node.right != null) nodes.add(node.right);
                return node.value;
            }
        };
    }

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
    public int maxDepth() {
        return maxDepth(root);
    }

    private int maxDepth(Node<T> node) {
        if (node == null) {
            return 0;
        } else {
            // compute the maxDepth of each subtree and take the greater one
            int lDepth = maxDepth(node.left);
            int rDepth = maxDepth(node.right);
            return Math.max(lDepth, rDepth) + 1;
        }
    }
}