package mycache;

import javafx.util.Pair;
import mycache.cachepolicies.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public abstract class SimpleCache<K, V> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(SimpleCache.class);
    private int maxSize;
    private Policy<K> policy;

    SimpleCache(int maxSize, Policy<K> policy) {
        if (maxSize <= 0){
            throw new IllegalArgumentException("Cache size should be >= 1");
        }
        log.debug(format("Initializing cache: [%s]; with policy: [%s] ", this.getClass().getCanonicalName(), policy.getClass().getName()));
        this.maxSize = maxSize;
        this.policy = policy;
    }

    @Override
    public synchronized V get(K key) {
        log.debug(format("Getting value from cache: [%s]; key = [%s] ", this.getClass().getName(), key));
        if (!contains(key)) {
            log.debug("Getting value from cache, value does not present in cache: [%s]; key = [%s] ", this.getClass().getName(), key);
            return null;
        }
        policy.onGet(key);
        return simpleGet(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        if (isFull()&& !contains(key)) {
            remove(policy.getWeakest());
        }
        putAndLog(key, value);
    }

    synchronized Pair<K, V> putAndReturnRemoved(K key, V value) {
        Pair<K, V> removedVal = null;
        if (isFull() && !contains(key)) {
            K keyToRemove = policy.getWeakest();
            removedVal = new Pair<>(keyToRemove, remove(keyToRemove));
        }
        putAndLog(key, value);
        return removedVal;
    }

    @Override
    public synchronized V remove(K key) {
        log.debug(format("Removing value from cache: [%s]; key = [%s] ", this.getClass().getName(), key));
        policy.onRemove(key);
        return simpleRemove(key);
    }

    @Override
    public synchronized void clean() {
        log.debug(format("Cleaning cache: [%s]", this.getClass().getName()));
        policy.onClean();
        simpleClean();
    }

    abstract V simpleGet(K key);

    abstract void simplePut(K key, V value);

    abstract V simpleRemove(K key);

    abstract void simpleClean();

    public abstract boolean contains(K key);

    public abstract int size();

    public boolean isFull() {
        return size() >= maxSize;
    }

    private void putAndLog(K key, V value) {
        log.debug(format("Putting value to cache: [%s]; key = [%s] ", this.getClass().getName(), key));
        policy.onPut(key);
        simplePut(key, value);
    }
}
