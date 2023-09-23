package fr.paulem.api.classes;

public class Pair<K, V> {
    private final K first;
    private final V second;

    public Pair(K k, V v) {
        first = k;
        second = v;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
