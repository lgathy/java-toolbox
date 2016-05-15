package com.doctusoft.dataops;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

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
            super(fHashCode);
        }

        @Override
        protected Entries<String, Integer> toEntries(Function<? super String, Integer> mapper, List<String> keys) {
            return Entries.forEntries(transform(mapper, keys));
        }
    }

    public static final class ListOfComputedValues extends TestRandomEntries<String> {

        public ListOfComputedValues() {
            super(fReverse);
        }

        @Override
        protected Entries<String, String> toEntries(Function<? super String, String> mapper, List<String> keys) {
            return Entries.lookupKeys(keys, mapper);
        }
    }

    public static final class ListOfIndexedValues extends TestRandomEntries<String> {

        public ListOfIndexedValues() {
            super(Functions.<String> identity());
        }

        @Override
        protected Entries<String, String> toEntries(Function<? super String, String> mapper, List<String> keys) {
            return Entries.indexValues(keys, Functions.<String> identity());
        }
    }

    private static <K, V> FluentIterable<Entry<K, V>> transform(final Function<? super K, V> mapper, List<K> keys) {
        return FluentIterable.from(keys).transform(new Function<K, Entry<K,V>>() {
            @Override
            public Entry<K, V> apply(K key) {
                return Maps.immutableEntry(key, mapper.apply(key));
            }
        });
    }

    private Function<? super String, V> mapper;

    private EntriesTester<String, V> entries;

    protected TestRandomEntries(Function<? super String, V> mapper) {
        this.mapper = mapper;
    }

    protected abstract Entries<String, V> toEntries(Function<? super String, V> mapper, List<String> keys);

    @Theory
    public void test(int count) {
        List<String> randomKeys = generateRandomStrings(count);
        entries = new EntriesTester<>(toEntries(mapper, randomKeys));
        for (String s : randomKeys) {
            entries.assertNext(s, mapper.apply(s));
        }
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

    private static final Function<Object, Integer> fHashCode = new Function<Object, Integer>() {

        @Override
        public Integer apply(Object obj) {
            return obj.hashCode();
        }
    };

    private static final Function<String, String> fReverse = new Function<String, String>() {

        @Override
        public String apply(String input) {
            return new StringBuilder(input).reverse().toString();
        }
    };
    
}
