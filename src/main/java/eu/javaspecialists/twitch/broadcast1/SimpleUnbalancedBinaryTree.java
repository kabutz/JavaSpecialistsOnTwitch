package eu.javaspecialists.twitch.broadcast1;

/**
 * A simple unbalanced binary tree implementation.
 *
 * @param <T> the type of elements in the tree, must implement the
 *            Comparable interface
 */
// see https://www.linkedin.com/video/live/urn:li:ugcPost:7171037281166835712/
// and https://www.linkedin.com/video/live/urn:li:ugcPost:7171047499154079745/
// and https://www.linkedin.com/video/live/urn:li:ugcPost:7173332983729274880/
public class SimpleUnbalancedBinaryTree<T extends Comparable<T>> extends AbstractBinaryTree<T> {
    private Node<T> root;

    @Override
    Node<T> root() {
        return root;
    }

    @Override
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

    @Override
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

    private static class Node<E extends Comparable<E>>
            implements AbstractBinaryTree.Node<E> {
        E value;
        Node<E> left;
        Node<E> right;

        Node(E value) {
            this.value = value;
            left = null;
            right = null;
        }

        @Override
        public AbstractBinaryTree.Node<E> left() {
            return left;
        }

        @Override
        public AbstractBinaryTree.Node<E> right() {
            return right;
        }

        @Override
        public E value() {
            return value;
        }
    }
}