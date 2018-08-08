import java.io.Serializable;

public interface Cache<K, V> {
    V get(K key);

    void put(K key, V value);

    void remove(K key);

    void clean();

    boolean isPresent(K key);

    int size();

    boolean isFull();
}
