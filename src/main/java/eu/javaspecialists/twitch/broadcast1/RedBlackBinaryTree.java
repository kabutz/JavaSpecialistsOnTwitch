package eu.javaspecialists.twitch.broadcast1;

import java.util.*;

// https://www.linkedin.com/video/live/urn:li:ugcPost:7175910155673366529/
public class RedBlackBinaryTree<T extends Comparable<T>> implements BinaryTree<T> {
    private Node<T> root;
    private int modCount = 0;

    // Define color constants
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // Node class is enhanced with a new attribute 'color'
    private static class Node<T extends Comparable<T>> {
        T value;
        Node<T> left;
        Node<T> right;
        boolean color;

        Node(T value) {
            this.value = value;
            left = null;
            right = null;
            color = RED;
        }
    }

    @Override
    public void add(T value) {
        modCount++;
        root = add(root, value);
        root.color = BLACK;
    }

    private Node<T> add(Node<T> current, T value) {
        if (current == null) {
            return new Node<>(value);
        }

        if (value.compareTo(current.value) < 0) {
            current.left = add(current.left, value);
        } else if (value.compareTo(current.value) > 0) {
            current.right = add(current.right, value);
        }

        if (isRed(current.right) && !isRed(current.left))
            current = rotateLeft(current);
        if (isRed(current.left) && isRed(current.left.left))
            current = rotateRight(current);
        if (isRed(current.left) && isRed(current.right))
            flipColors(current);

        return current;
    }

    // Color helper
    private boolean isRed(Node<T> node) {
        if (node == null) return false;
        return node.color == RED;
    }

    // Rotation helpers
    private Node<T> rotateRight(Node<T> h) {
        Node<T> x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        return x;
    }

    private Node<T> rotateLeft(Node<T> h) {
        Node<T> x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        return x;
    }

    // Color flipping
    private void flipColors(Node<T> h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
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
    public void remove(T value) {
        if (contains(value)) {
            root = remove(root, value);
            if (root != null) {
                root.color = BLACK;
            }
            modCount++;
        }
    }

    private Node<T> remove(Node<T> current, T value) {
        if (value.compareTo(current.value) < 0) {
            if (current.left == null) {
                return null;
            }
            if (!isRed(current.left) && !isRed(current.left.left)) {
                current = moveRedLeft(current);
            }
            current.left = remove(current.left, value);
        } else {
            if (isRed(current.left)) {
                current = rotateRight(current);
            }
            if (value.compareTo(current.value) == 0 && (current.right == null)) {
                return null;
            }
            if (!isRed(current.right) && !isRed(current.right.left)) {
                current = moveRedRight(current);
            }
            if (value.compareTo(current.value) == 0) {
                Node<T> x = min(current.right);
                current.value = x.value;
                current.right = deleteMin(current.right);
            } else {
                current.right = remove(current.right, value);
            }
        }
        return balance(current);
    }

    private Node<T> moveRedLeft(Node<T> h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }

    private Node<T> moveRedRight(Node<T> h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
        }
        return h;
    }

    private Node<T> balance(Node<T> h) {
        if (isRed(h.right)) {
            h = rotateLeft(h);
        }
        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }
        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }
        return h;
    }

    private Node<T> deleteMin(Node<T> h) {
        if (h.left == null) {
            return null;
        }
        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }
        h.left = deleteMin(h.left);
        return balance(h);
    }

    private Node<T> min(Node<T> h) {
        if (h.left == null) {
            return h;
        } else {
            return min(h.left);
        }
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

    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    private class BinaryTreeIterator implements Iterator<T> {
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
}