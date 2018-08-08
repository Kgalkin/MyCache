import java.util.concurrent.ConcurrentHashMap;

public class MyInMemoryCache<K, T> {
    private final ConcurrentHashMap<K, T> memoryCache = new ConcurrentHashMap<K, T>();

    public T get(K key) {
        return memoryCache.get(key);
    }

    public void put(K key, T value) {
        memoryCache.put(key, value);
    }

    public int size(){
        return memoryCache.size();
    }

    public void clean(){
        memoryCache.clear();
    }
}
