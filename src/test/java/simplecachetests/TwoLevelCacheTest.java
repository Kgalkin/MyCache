package simplecachetests;

import mycache.Cache;
import mycache.TwoLevelCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TwoLevelCacheTest {
    private Cache<String, Integer> twoLevelCache;
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final String FOUR = "four";
    private static final String FIVE = "five";
    private static final String SIX = "six";
    private static final String SEVEN = "seven";

    @Before
    public void setCache(){
        twoLevelCache = new TwoLevelCache<>(3 ,3);
        twoLevelCache.put(ONE, 1);
        twoLevelCache.put(TWO, 2);
        twoLevelCache.put(THREE, 3);
        twoLevelCache.put(FOUR, 4);
        twoLevelCache.put(FIVE, 5);
        twoLevelCache.put(SIX, 6);
    }

    @After
    public void cleanCache(){
        twoLevelCache.clean();
    }

    @Test
    public void testGetFromCache(){
        Assert.assertEquals(1, twoLevelCache.get(ONE).intValue());
        Assert.assertEquals(6, twoLevelCache.get(SIX).intValue());
        Assert.assertNull(twoLevelCache.get(SEVEN));
    }

    @Test
    public void testRemoveFromCache(){
        twoLevelCache.remove(ONE);
        twoLevelCache.remove(SIX);
        Assert.assertNull(twoLevelCache.get(ONE));
        Assert.assertNull(twoLevelCache.get(SIX));
    }

    @Test
    public void testUpdateCache(){
        twoLevelCache.put(ONE, 10);
        Assert.assertEquals(10,twoLevelCache.get(ONE).intValue());
        Assert.assertEquals(6, twoLevelCache.get(SIX).intValue());
        twoLevelCache.put(SIX, 60);
        Assert.assertEquals(60, twoLevelCache.get(SIX).intValue());
        Assert.assertEquals(6, twoLevelCache.size());
    }

    @Test
    public void testCacheOverfull(){
        twoLevelCache.put(SEVEN, 7);
        Assert.assertEquals(6, twoLevelCache.size());
        Assert.assertNull(twoLevelCache.get(ONE));
    }
}
