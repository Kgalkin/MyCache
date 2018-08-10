package mycache;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class TwoLevelCache<K, V extends Serializable> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(TwoLevelCache.class);
    private final SimpleCache<K, V> firstLevelCache;
    private final SimpleCache<K, V> secondLevelCache;

    public TwoLevelCache() {
        this(new MemoryCache<>(), new MyFileSystemCache<>());
    }

    public TwoLevelCache(SimpleCache<K, V> firstLevelCache, SimpleCache<K, V> secondLevelCache) {
        this.firstLevelCache = firstLevelCache;
        this.secondLevelCache = secondLevelCache;
    }

    @Override
    public V get(K key) {
        return (firstLevelCache.contains(key) ? firstLevelCache : secondLevelCache).get(key);
    }

    @Override
    public void put(K key, V value) {
        Pair<K, V> removed = firstLevelCache.putAndReturnRemoved(key, value);
        if (removed != null) {
            secondLevelCache.put(removed.getKey(), removed.getValue());
        }
    }

    @Override
    public V remove(K key) {
        return (firstLevelCache.contains(key) ? firstLevelCache : secondLevelCache).remove(key);
    }

    @Override
    public void clean() {
        firstLevelCache.clean();
        secondLevelCache.clean();
    }
}
