package com.doctusoft.dataops;

import org.junit.Test;

import java.util.*;

public abstract class TestDistinctEntries {

    public static final class ForMap extends TestDistinctEntries {
        @Override
        protected <K, V> Entries<K, V> toEntries(Map<K, V> map) {
            return Entries.forMap(map);
        }
    }

    public static final class ForEntrySet extends TestDistinctEntries {
        @Override
        protected <K, V> Entries<K, V> toEntries(Map<K, V> map) {
            return Entries.forEntries(map.entrySet());
        }
    }

    private EntriesTester<Object, Object> entries;

    protected abstract <K, V> Entries<K, V> toEntries(Map<K, V> map);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void createTester(Map<?, ?> map) {
        entries = new EntriesTester(toEntries(map));
    }

    @Test
    public void smokeTest() {
        createTester(createMap("key", "value"));
        entries.assertNext("key", "value");
        entries.assertNoMore();
    }

    @Test
    public void testEmptyMap() {
        createTester(new HashMap<>());
        entries.assertNoMore();
    }

    @Test
    public void testKeyValues() {
        createTester(createMap("Java", 4, "Python", 6, "Go", 2, "Ruby", 4, "Scala", 5));
        entries.assertNext("Java", 4);
        entries.assertNext("Python", 6);
        entries.assertNext("Go", 2);
        entries.assertNext("Ruby", 4);
        entries.assertNext("Scala", 5);
        entries.assertNoMore();
    }

    private static Map<?, ?> createMap(Object... keyValues) {
        Map<Object, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; ) {
            Object key = keyValues[i++];
            Object value = keyValues[i++];
            map.put(key, value);
        }
        return map;
    }
    
}
