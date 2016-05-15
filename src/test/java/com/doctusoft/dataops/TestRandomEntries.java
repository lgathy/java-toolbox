package com.doctusoft.dataops;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

@RunWith(Theories.class)
public abstract class TestRandomEntries<V> {
    
    @DataPoint
    public static int ZERO = 0;
    @DataPoint
    public static int ONE = 1;
    @DataPoint
    public static int TWO = 2;
    @DataPoint
    public static int SOME = 13;
    @DataPoint
    public static int ALOT = 350;
    @DataPoint
    public static int HUGE = 50_000;
    
    public static final class ListOfEntries extends TestRandomEntries<Integer> {
        
        public ListOfEntries() {
            super(String::hashCode);
        }
        
        @Override
        protected Entries<String, Integer> toEntries(Function<String, Integer> mapper, List<String> keys) {
            return Entries.forEntries(toEntryStream(mapper, keys).collect(Collectors.toList()));
        }
    }
    
    public static final class StreamOfEntries extends TestRandomEntries<Integer> {
        
        public StreamOfEntries() {
            super(String::length);
        }
        
        @Override
        protected Entries<String, Integer> toEntries(Function<String, Integer> mapper, List<String> keys) {
            return Entries.forEntryStream(toEntryStream(mapper, keys));
        }
    }
    
    public static final class StreamOfComputedValues extends TestRandomEntries<String> {
        
        public StreamOfComputedValues() {
            super(s -> new StringBuilder(s).reverse().toString());
        }
        
        @Override
        protected Entries<String, String> toEntries(Function<String, String> mapper, List<String> keys) {
            return Entries.lookupKeyStream(keys.stream().sequential(), mapper);
        }
    }
    
    public static final class StreamOfIndexedValues extends TestRandomEntries<String> {
        
        public StreamOfIndexedValues() {
            super(Function.identity());
        }
        
        @Override
        protected Entries<String, String> toEntries(Function<String, String> mapper, List<String> keys) {
            return Entries.indexValues(keys, Function.identity());
        }
    }
    
    private static <K, V> Stream<Entry<K, V>> toEntryStream(Function<K, V> mapper, List<K> keys) {
        return keys
            .stream()
            .sequential()
            .map(k -> new AbstractMap.SimpleImmutableEntry<>(k, mapper.apply(k)));
    }
    
    private Function<String, V> mapper;
    
    private EntriesTester<String, V> entries;
    
    protected TestRandomEntries(Function<String, V> mapper) {
        this.mapper = mapper;
    }

    protected abstract Entries<String, V> toEntries(Function<String, V> mapper, List<String> keys);
    
    @Theory
    public void test(int count) {
        List<String> randomKeys = generateRandomStrings(count);
        entries = new EntriesTester<>(toEntries(mapper, randomKeys));
        randomKeys.forEach(s -> entries.assertNext(s, mapper.apply(s)));
        entries.assertNoMore();
    }
    
    private List<String> generateRandomStrings(int count) {
        Random random = new Random();
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            list.add(String.valueOf(random.nextInt()));
        }
        return list;
    }
    
}
