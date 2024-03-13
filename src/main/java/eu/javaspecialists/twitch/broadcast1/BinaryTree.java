package eu.javaspecialists.twitch.broadcast1;

public interface BinaryTree<T extends Comparable<T>> extends Iterable<T> {
    void add(T value);

    boolean contains(T value);

    void remove(T value);

    int size();

    int maxDepth();
}
