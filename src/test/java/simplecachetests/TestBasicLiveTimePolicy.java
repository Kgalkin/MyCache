package simplecachetests;

import mycache.FileSystemCache;
import mycache.MemoryCache;
import mycache.SimpleCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestBasicLiveTimePolicy {
    private final SimpleCache<String, Integer> cache;
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final String FOUR = "four";

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { {new MemoryCache<>(3)}, {new FileSystemCache<>(3)}});
    }

    public TestBasicLiveTimePolicy(SimpleCache<String, Integer> cache) {
        this.cache = cache;
    }

    @Before
    public void setCache() {
        cache.put(ONE, 1);
        cache.put(TWO, 2);
        cache.put(THREE, 3);
    }

    @After
    public void clearCache() {
        cache.clean();
    }

    @Test
    public void testRemovePolicy() {
        cache.put(FOUR, 4);
        Assert.assertEquals(3, cache.size());
        Assert.assertFalse(cache.contains(ONE));
        Assert.assertNull(cache.get(ONE));
        Assert.assertEquals(2, cache.get(TWO).intValue());
        Assert.assertEquals(3, cache.get(THREE).intValue());
        Assert.assertEquals(4, cache.get(FOUR).intValue());
    }
}
