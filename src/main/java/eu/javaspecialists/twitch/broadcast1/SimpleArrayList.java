package eu.javaspecialists.twitch.broadcast1;

import java.util.*;

// See https://www.linkedin.com/video/live/urn:li:ugcPost:7169345856473653248/
public class SimpleArrayList<T> implements SimpleList<T> {
    private Object[] elements;
    private int size = 0;
    private int modCount = 0;

    public SimpleArrayList() {
        elements = new Object[10];
    }

    @Override
    public void add(T element) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, 2 * size);
        }
        elements[size] = element;
        size++;
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T)elements[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void removeAt(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        size--;
        elements[size] = null;
        modCount++;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int position = 0;
            private int expectedModCount = modCount;
            private boolean nextCalled = false;

            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            public T next() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                nextCalled = true;
                return (T) elements[position++];
            }

            @Override
            public void remove() {
                if (this.position < 0 || !nextCalled)
                    throw new IllegalStateException();
                if (this.expectedModCount != modCount)
                    throw new ConcurrentModificationException();
                try {
                    SimpleArrayList.this.removeAt(position - 1);
                    this.position = this.position - 1;
                    this.expectedModCount = modCount;
                    nextCalled = false;
                } catch (IndexOutOfBoundsException ex) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }
}