package eu.javaspecialists.twitch.broadcast1;

public interface BinaryTree<T extends Comparable<T>> extends Iterable<T> {
    void add(T value);

    boolean contains(T value);

    void remove(T value);

    default int size() {
        int size = 0;
        for (T t : this) {
            size++;
        }
        return size;
    }

    int maxDepth();
}
