package simplecachetests;

import mycache.Cache;
import mycache.FileSystemCache;
import mycache.MemoryCache;
import mycache.cachepolicies.LeastUsedOrLiveTimePolicy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestLeastUsedOrLiveTimePolicy {
    private final Cache<String, Integer> cache;
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final String FOUR = "four";

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { {new MemoryCache<>(3, new LeastUsedOrLiveTimePolicy<>())}, {new FileSystemCache<>(3, new LeastUsedOrLiveTimePolicy<>())}});
    }

    public TestLeastUsedOrLiveTimePolicy(Cache<String, Integer> cache){
        this.cache = cache;
    }

    @Before
    public void setCache() {
        cache.put(ONE, 1);
        cache.put(TWO, 2);
        cache.put(THREE, 3);
    }

    @After
    public void cleanCache() {
        cache.clean();
    }

    @Test
    public void testRemovePolicy1() {
        cache.put(FOUR, 4);
        Assert.assertNull(cache.get(ONE));
    }

    @Test
    public void testRemovePolicy2() {
        cache.get(ONE);
        cache.put(FOUR, 4);
        Assert.assertEquals(1, cache.get(ONE).intValue());
        Assert.assertNull(cache.get(TWO));
    }

    @Test
    public  void testRemovePolicy3() {
        cache.get(ONE);
        cache.get(TWO);
        cache.get(TWO);
        cache.get(THREE);
        cache.put(FOUR, 4);
        Assert.assertNull(cache.get(ONE));
    }
}
