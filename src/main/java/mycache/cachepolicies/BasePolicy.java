package mycache.cachepolicies;

import java.util.HashMap;
import java.util.Map;

public class BasePolicy<K> implements Policy<K, Long> {
private final Map<K, Long> creationTime = new HashMap<>();

    @Override
    public K getWeakest() {
        //TODO: replace with sorted map
        return creationTime.entrySet().stream().reduce((a, b)-> a.getValue()<b.getValue()? a : b).orElseThrow(()-> new RuntimeException("can not specify weakest key")).getKey();
    }

    @Override
    public K getStrongest(){
        //TODO: replace with sorted map
        return creationTime.entrySet().stream().reduce((a, b)-> a.getValue()>b.getValue()? a : b).orElseThrow(()-> new RuntimeException("can not specify weakest key")).getKey();
    }

    @Override
    public void onPut(K key) {
        creationTime.put(key, System.nanoTime());
    }

    @Override
    public void onPut(K key, Long time){
        creationTime.put(key, time);
    }

    @Override
    public void onGet(K key){

    }

    @Override
    public void onRemove(K key){
        creationTime.remove(key);
    }

    @Override
    public void onClean(){
        creationTime.clear();
    }

    @Override
    public Map<K, Long> getMap() {
        return creationTime;
    }
}
