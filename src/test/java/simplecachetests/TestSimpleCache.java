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
public class TestSimpleCache {
    private final SimpleCache<String, Integer> cache;
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {new MemoryCache<>(3)},
                {new FileSystemCache<>(3)}});
    }

    public TestSimpleCache(SimpleCache<String, Integer> cache) {
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
    public void testGetNotExisted(){
        Assert.assertFalse(cache.contains("hello"));
        Assert.assertNull(cache.get("hello"));
    }

    @Test
    public void testReplace(){
        cache.put(TWO, 100);
        Assert.assertEquals(100, cache.get(TWO).intValue());
    }

    @Test
    public void testGetFromCache() {
        Assert.assertEquals(1, cache.get(ONE).intValue());
        Assert.assertEquals(2, cache.get(TWO).intValue());
        Assert.assertEquals(3, cache.get(THREE).intValue());
    }

    @Test
    public void testRemoveFromCache(){
        Integer removed = cache.remove(ONE);
        Assert.assertFalse(cache.contains(ONE));
        Assert.assertNull(cache.get(ONE));
        Assert.assertEquals(1, removed.intValue());
    }

    @Test
    public void isFullTest(){
        Assert.assertTrue(cache.isFull());
        cache.remove(ONE);
        Assert.assertFalse(cache.isFull());
    }

    @Test
    public void testUpdateCache(){
        cache.put(THREE, 30);
        Assert.assertEquals(30, cache.get(THREE).intValue());
        Assert.assertEquals(3, cache.size());
    }
}
