package eu.javaspecialists.twitch.broadcast1;

import java.util.*;

// https://www.linkedin.com/video/live/urn:li:ugcPost:7176207737867956225/
abstract class AbstractBinaryTree<T extends Comparable<T>> implements BinaryTree<T> {
    abstract Node<T> root();

    int modCount = 0;

    @Override
    public final boolean contains(T value) {
        return contains(root(), value);
    }

    private boolean contains(Node<T> current, T value) {
        if (current == null) {
            return false;
        }
        if (value.compareTo(current.value()) == 0) {
            return true;
        }
        return value.compareTo(current.value()) < 0
                ? contains(current.left(), value)
                : contains(current.right(), value);
    }

    interface Node<E extends Comparable<E>> {
        Node<E> left();

        Node<E> right();

        E value();
    }


    @Override
    public final Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    private class BinaryTreeIterator implements Iterator<T> {
        private final int initialModCount = modCount;
        private final Deque<Node<T>> nodeStack = new ArrayDeque<>();

        {
            if (root() != null) {
                pushLeftSubtree(root());
            }
        }

        private void pushLeftSubtree(Node<T> node) {
            while (node != null) {
                nodeStack.push(node);
                node = node.left();
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
            pushLeftSubtree(nextNode.right());

            return nextNode.value();
        }

        private void checkForComodification() {
            if (modCount != initialModCount)
                throw new ConcurrentModificationException();
        }
    }


    /**
     * Method to measure the maxDepth of the tree.
     */
    @Override
    public final int maxDepth() {
        if (root() == null) {
            return 0;
        }

        var deque = new ArrayDeque<Pair<Node<T>, Integer>>();
        deque.push(new Pair<>(root(), 1));
        int maxDepth = 0;

        while (!deque.isEmpty()) {
            Pair<Node<T>, Integer> current = deque.pop();
            Node<T> node = current.first;
            int currentDepth = current.second;

            if (node != null) {
                maxDepth = Math.max(maxDepth, currentDepth);
                if (node.right() != null) {
                    deque.push(new Pair<>(node.right(), currentDepth + 1));
                }
                if (node.left() != null) {
                    deque.push(new Pair<>(node.left(), currentDepth + 1));
                }
            }
        }
        return maxDepth;
    }

    private record Pair<U, V>(U first, V second) {
    }
}