package mycache.cachepolicies;

import org.slf4j.LoggerFactory;

import java.util.*;

public class LiveTimePolicy<K> implements Policy<K> {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LiveTimePolicy.class);
    private final Map<K, Long> policyMap = new HashMap<>();

    @Override
    public K getWeakest() {
        return policyMap.entrySet()
                .stream()
                .reduce((a, b) -> a.getValue() < b.getValue() ? a : b)
                .orElseGet(() -> {
                    log.error("Can not specify weakest key, random key would be provided");
                    return policyMap.entrySet().stream().findFirst().orElseThrow(() -> new IllegalStateException("policyMap is empty cannot specify weakest item"));
                })
                .getKey();
    }

    @Override
    public void onPut(K key) {
        policyMap.put(key, System.nanoTime());
    }

    @Override
    public void onGet(K key) {

    }

    @Override
    public void onRemove(K key) {
        policyMap.remove(key);
    }

    @Override
    public void onClean() {
        policyMap.clear();
    }
}
