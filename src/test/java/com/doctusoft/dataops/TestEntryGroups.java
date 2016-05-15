package com.doctusoft.dataops;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.*;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.assertEquals;

public final class TestEntryGroups {

    @Test
    public void testEmpty() {
        new EntriesTester<>(indexGroups(newArrayList, fLength)).assertNoMore();
    }

    @Test
    public void testSingleton() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(newArrayList, fLength, "Java"));
        groups.assertNext(4, asList("Java"));
        groups.assertNoMore();
    }

    @Test
    public void testUnsorted() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(
            newArrayList, fLength, "Java", "Python", "Go", "Ruby", "Scala"));
        groups.assertNext(4, asList("Java"));
        groups.assertNext(6, asList("Python"));
        groups.assertNext(2, asList("Go"));
        groups.assertNext(4, asList("Ruby"));
        groups.assertNext(5, asList("Scala"));
        groups.assertNoMore();
    }

    @Test
    public void testSorted() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(
            newArrayList, fLength, "Go", "Ruby", "Java", "Scala", "Python"));
        groups.assertNext(2, asList("Go"));
        groups.assertNext(4, asList("Ruby", "Java"));
        groups.assertNext(5, asList("Scala"));
        groups.assertNext(6, asList("Python"));
        groups.assertNoMore();
    }

    @Test
    public void testDuplicatesInList() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(
            newArrayList, fLength, "Go", "Ruby", "Java", "Ruby", "Scala", "Dart", "Python"));
        groups.assertNext(2, asList("Go"));
        groups.assertNext(4, asList("Ruby", "Java", "Ruby"));
        groups.assertNext(5, asList("Scala"));
        groups.assertNext(4, asList("Dart"));
        groups.assertNext(6, asList("Python"));
        groups.assertNoMore();
    }

    @Test
    public void testNoDuplicatesInSet() {
        EntriesTester<Integer, Set<String>> groups = new EntriesTester<>(indexGroups(
            newHashSet, fLength, "Go", "JS", "Scala", "Python", "Java", "Ruby"));
        groups.assertNext(2, asSet("Go", "JS"));
        groups.assertNext(5, singleton("Scala"));
        groups.assertNext(6, singleton("Python"));
        groups.assertNext(4, asSet("Java", "Ruby"));
        groups.assertNoMore();
    }

    @Test
    public void testCustomEquality() {
        String[] values = { "Dart", "jUnit", "Java", "JS", "Scala", "Spring" };
        EntriesTester<String, Set<String>> groups;

        groups = new EntriesTester<>(new EntryGroups<>(indexByFirstLetter(values), newHashSet));
        groups.assertNext("D", singleton("Dart"));
        groups.assertNext("j", singleton("jUnit"));
        groups.assertNext("J", asSet("Java", "JS"));
        groups.assertNext("S", asSet("Spring", "Scala"));
        groups.assertNoMore();

        groups = new EntriesTester<>(new EntryGroups<>(indexByFirstLetter(values), newHashSet, equalsIgnoreCase));
        groups.assertNext("D", singleton("Dart"));
        groups.assertNext("j", asSet("Java", "JS", "jUnit"));
        groups.assertNext("S", asSet("Spring", "Scala"));
        groups.assertNoMore();
    }

    private static final Set<String> asSet(String... values) {
        HashSet<String> set = new HashSet<>(asList(values));
        assertEquals("Non unique elements: " + asList(values), values.length, set.size());
        return set;
    }

    private static final <K, G extends Collection<String>> EntryGroups<K, String, G> indexGroups(
        Supplier<? extends G> collFactory, Function<String, ? extends K> mapper, String... values) {
        return new EntryGroups<>(Entries.indexValues(Arrays.asList(values), mapper), collFactory);
    }

    private static final Entries<String, String> indexByFirstLetter(String... values) {
        return Entries.indexValues(Arrays.asList(values), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.substring(0, 1);
            }
        });
    }

    private static final Function<String, Integer> fLength = new Function<String, Integer>() {

        @Override
        public Integer apply(String input) {
            return input.length();
        }
    };

    private static final Supplier<List<String>> newArrayList = new Supplier<List<String>>() {

        @Override
        public List<String> get() {
            return new ArrayList<>();
        }
    };

    private static final Supplier<Set<String>> newHashSet = new Supplier<Set<String>>() {

        @Override
        public Set<String> get() {
            return new HashSet<>();
        }
    };

    private static final Equivalence<String> equalsIgnoreCase = new Equivalence<String>() {

        @Override
        protected boolean doEquivalent(String a, String b) {
            return a.equalsIgnoreCase(b);
        }

        @Override
        protected int doHash(String t) {
            return t.hashCode();
        }

    };
    
}
