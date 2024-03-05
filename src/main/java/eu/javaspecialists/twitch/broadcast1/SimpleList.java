package eu.javaspecialists.twitch.broadcast1;

public interface SimpleList<T> extends Iterable<T> {
    void add(T element);

    T get(int index);

    int size();

    void removeAt(int index);
}
