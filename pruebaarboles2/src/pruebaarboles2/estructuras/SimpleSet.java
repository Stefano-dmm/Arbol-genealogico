package pruebaarboles2.estructuras;

public class SimpleSet<T> {
    private Object[] elements;
    private int size;
    private static final int INITIAL_CAPACITY = 16;

    public SimpleSet() {
        elements = new Object[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(T element) {
        if (!contains(element)) {
            if (size >= elements.length) {
                resize();
            }
            elements[size++] = element;
        }
    }

    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return true;
            }
        }
        return false;
    }

    private void resize() {
        Object[] newElements = new Object[elements.length * 2];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        elements = newElements;
    }

    public String[] toArray() {
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = (String) elements[i];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public T[] toGenericArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        }
        System.arraycopy(elements, 0, a, 0, size);
        return a;
    }

    public int size() {
        return size;
    }
} 