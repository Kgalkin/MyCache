package mycache.cachepolicies;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LeastUsedOrLiveTimePolicy<K> implements Policy<K> {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LeastUsedOrLiveTimePolicy.class);

    private final Map<K, TimeAndNumberOfGets> policyMap = new HashMap<>();

    @Override
    public K getWeakest() {
        return policyMap.entrySet()
                .stream()
                .reduce((a, b) -> a.getValue().compareTo(b.getValue()) > 0 ? b : a)
                .orElseGet(() -> {
                    log.error("Can not specify weakest key, random key would be provided");
                    return policyMap.entrySet().stream().findFirst().orElseThrow(() -> new IllegalStateException("policyMap is empty cannot specify weakest item"));
                })
                .getKey();
    }

    @Override
    public void onPut(K key) {
        policyMap.put(key, new TimeAndNumberOfGets(System.nanoTime()));
    }

    @Override
    public void onGet(K key) {
        if (policyMap.containsKey(key)) {
            policyMap.get(key).getsNumber++;
        }
    }

    @Override
    public void onRemove(K key) {
        policyMap.remove(key);
    }

    @Override
    public void onClean() {
        policyMap.clear();
    }

    private static class TimeAndNumberOfGets implements Comparable<TimeAndNumberOfGets> {
        private int getsNumber;
        private final long creationTime;

        private TimeAndNumberOfGets(long creationTime) {
            this.getsNumber = 1;
            this.creationTime = creationTime;
        }

        @Override
        public int compareTo(TimeAndNumberOfGets other) {
            if (this.getsNumber == other.getsNumber) {
                return (int) (this.creationTime - other.creationTime);
            }
            return this.getsNumber > other.getsNumber ? 1 : -1;
        }
    }
}
