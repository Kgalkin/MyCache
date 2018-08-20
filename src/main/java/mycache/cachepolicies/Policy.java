package mycache.cachepolicies;

public interface Policy<K>{
    K getWeakest();
    void onPut(K key);
    void onGet(K key);
    void onRemove(K key);
    void onClean();
}
