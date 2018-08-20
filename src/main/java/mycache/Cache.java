package mycache;

public interface Cache<K, V> {

    V get(K key);

    void put(K key, V value);

    V remove(K key);

    void clean();

    int size();

}
