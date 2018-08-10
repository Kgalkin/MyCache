package mycache.cachepolicies;

import java.util.Map;

public interface Policy<K, P>{
    K getWeakest();
    K getStrongest();
    Map<K, P> getMap();
    void onPut(K key);
    void onPut(K key, P parameter);
    void onGet(K key);
    void onRemove(K key);
    void onClean();
}
