package mycache;

import mycache.cachepolicies.BasePolicy;
import mycache.cachepolicies.Policy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class MemoryCache<K, V>  extends SimpleCache<K, V> {
    private ConcurrentMap<K, V> cacheMap;


    public MemoryCache(){
        this(10);
    }

    public MemoryCache(int maxSize) {
       this(maxSize, new BasePolicy<>());
    }

    public MemoryCache(int maxSize, Policy<K, ?> policy){
        super(maxSize, policy);
        cacheMap = new ConcurrentHashMap<>(maxSize);
    }

    @Override
    V simpleGet(K key) {
        return cacheMap.get(key);
    }

    @Override
    void simplePut(K key, V value) {
        cacheMap.put(key, value);
    }

    @Override
    V simpleRemove(K key) {
       return cacheMap.remove(key);
    }

    @Override
    void simpleClean() {
        cacheMap.clear();
    }

    public int size() {
        return cacheMap.size();
    }

    public boolean contains(K key) {
        return cacheMap.containsKey(key);
    }
}
