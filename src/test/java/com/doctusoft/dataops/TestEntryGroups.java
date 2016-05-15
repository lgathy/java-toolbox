package com.doctusoft.dataops;

import org.junit.Test;

import java.util.ArrayList;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.assertEquals;

public final class TestEntryGroups {
    
    @Test
    public void testEmpty() {
        new EntriesTester<>(indexGroups(ArrayList::new, String::length)).assertNoMore();
    }
    
    @Test
    public void testSingleton() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(
            ArrayList::new, String::length, "Java"));
        groups.assertNext(4, asList("Java"));
        groups.assertNoMore();
    }
    
    @Test
    public void testUnsorted() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(
            ArrayList::new, String::length, "Java", "Python", "Go", "Ruby", "Scala"));
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
            ArrayList::new, String::length, "Go", "Ruby", "Java", "Scala", "Python"));
        groups.assertNext(2, asList("Go"));
        groups.assertNext(4, asList("Ruby", "Java"));
        groups.assertNext(5, asList("Scala"));
        groups.assertNext(6, asList("Python"));
        groups.assertNoMore();
    }
    
    @Test
    public void testDuplicatesInList() {
        EntriesTester<Integer, List<String>> groups = new EntriesTester<>(indexGroups(
            ArrayList::new, String::length, "Go", "Ruby", "Java", "Ruby", "Scala", "Dart", "Python"));
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
            HashSet::new, String::length, "Go", "JS", "Scala", "Python", "Java", "Ruby"));
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
        
        groups = new EntriesTester<>(new EntryGroups<>(indexByFirstLetter(values), HashSet::new));
        groups.assertNext("D", singleton("Dart"));
        groups.assertNext("j", singleton("jUnit"));
        groups.assertNext("J", asSet("Java", "JS"));
        groups.assertNext("S", asSet("Spring", "Scala"));
        groups.assertNoMore();
        
        groups = new EntriesTester<>(
            new EntryGroups<>(indexByFirstLetter(values), HashSet::new, String::equalsIgnoreCase));
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
        return new EntryGroups<>(Entries.indexValueStream(Stream.of(values), mapper), collFactory);
    }
    
    private static final Entries<String, String> indexByFirstLetter(String... values) {
        return Entries.indexValueStream(Stream.of(values), s -> s.substring(0, 1));
    }
    
}
