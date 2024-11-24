package pruebaarboles2.estructuras;

public class SimpleMap<K,V> {
    private Entry<K,V>[] entries;
    private int size;
    private static final int INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public SimpleMap() {
        entries = new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        
        for (int i = 0; i < size; i++) {
            if (entries[i].key.equals(key)) {
                entries[i].value = value;
                return;
            }
        }
        
        if (size >= entries.length) {
            resize();
        }
        
        entries[size++] = new Entry<>(key, value);
    }

    public V get(K key) {
        if (key == null) {
            return null;
        }
        
        for (int i = 0; i < size; i++) {
            if (entries[i].key.equals(key)) {
                return entries[i].value;
            }
        }
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    public void putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
        }
    }

    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        
        for (int i = 0; i < size; i++) {
            if (entries[i].key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Entry<K,V>[] entrySet() {
        @SuppressWarnings("unchecked")
        Entry<K,V>[] result = new Entry[size];
        System.arraycopy(entries, 0, result, 0, size);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K,V>[] newEntries = new Entry[entries.length * 2];
        System.arraycopy(entries, 0, newEntries, 0, entries.length);
        entries = newEntries;
    }

    public static class Entry<K,V> {
        public K key;
        public V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
} 