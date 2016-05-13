package com.doctusoft.dataops;

import org.junit.Test;

import java.util.*;
import java.util.function.*;

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
    
    public static final class ForEntryStream extends TestDistinctEntries {
        @Override
        protected <K, V> Entries<K, V> toEntries(Map<K, V> map) {
            return Entries.forEntryStream(map.entrySet().stream());
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
        createTester(createMap(HashMap::new, "key", "value"));
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
        createTester(createMap(LinkedHashMap::new, "Java", 4, "Python", 6, "Go", 2, "Ruby", 4, "Scala", 5));
        entries.assertNext("Java", 4);
        entries.assertNext("Python", 6);
        entries.assertNext("Go", 2);
        entries.assertNext("Ruby", 4);
        entries.assertNext("Scala", 5);
        entries.assertNoMore();
    }
    
    @SuppressWarnings("rawtypes")
    private static Map<?, ?> createMap(Supplier<? extends Map> factory, Object... keyValues) {
        Map<Object, Object> map = factory.get();
        for (int i = 0; i < keyValues.length;) {
            Object key = keyValues[i++];
            Object value = keyValues[i++];
            map.put(key, value);
        }
        return map;
    }
    
}
