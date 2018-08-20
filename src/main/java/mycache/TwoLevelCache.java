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
        this(new MemoryCache<>(), new FileSystemCache<>());
    }

    public TwoLevelCache(int firstLevelSize, int secondLevelSize) {
        this(new MemoryCache<>(firstLevelSize), new FileSystemCache<>(secondLevelSize));
    }

    public TwoLevelCache(SimpleCache<K, V> firstLevelCache, SimpleCache<K, V> secondLevelCache) {
        log.debug("Initializing TwoLevelCache with caches: 1stLevel: [{}]; 2ndLevel: [{}]", firstLevelCache.getClass().getName(), secondLevelCache.getClass().getName());
        this.firstLevelCache = firstLevelCache;
        this.secondLevelCache = secondLevelCache;
    }

    @Override
    public V get(K key) {
        return (firstLevelCache.contains(key) ? firstLevelCache : secondLevelCache).get(key);
    }

    @Override
    public void put(K key, V value) {
        if (secondLevelCache.contains(key)) {
            secondLevelCache.remove(key);
        }
        Pair<K, V> removed = firstLevelCache.putAndReturnRemoved(key, value);
        if (removed != null) {
            log.debug("Moving object to 2nd level: key = [{}]", removed.getKey());
            secondLevelCache.put(removed.getKey(), removed.getValue());
        }
    }

    @Override
    public V remove(K key) {
        return (firstLevelCache.contains(key) ? firstLevelCache : secondLevelCache).remove(key);
    }

    @Override
    public void clean() {
        log.debug("Cleaning Two Level Cache");
        firstLevelCache.clean();
        secondLevelCache.clean();
    }

    @Override
    public int size(){
        return firstLevelCache.size() + secondLevelCache.size();
    }
}
