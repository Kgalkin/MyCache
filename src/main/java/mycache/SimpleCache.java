package mycache;

import javafx.util.Pair;
import mycache.cachepolicies.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleCache<K, V> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(SimpleCache.class);
    private int maxSize;
    private Policy<K, ?> policy;

    SimpleCache(int maxSize, Policy<K, ?> policy) {
        this.maxSize = maxSize;
        this.policy = policy;
    }

    @Override
    public synchronized V get(K key) {
        if (!contains(key)) {
            log.debug("Trying to get by key that does not present in cache: $s ", key);
            return null;
        }
        policy.onGet(key);
        return simpleGet(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        if(isFull()){
            remove(policy.getWeakest());
        }
        policy.onPut(key);
        simplePut(key, value);
    }

    synchronized Pair<K, V> putAndReturnRemoved(K key, V value) {
        Pair<K, V> removedVal = null;
        if (isFull()) {
            K keyToRemove = policy.getWeakest();
            removedVal = new Pair<>(keyToRemove, remove(keyToRemove));
        }
        policy.onPut(key);
        simplePut(key, value);
        return removedVal;
    }

    @Override
    public synchronized V remove(K key) {
        policy.onRemove(key);
        return simpleRemove(key);
    }

    @Override
    public synchronized void clean() {
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
}
