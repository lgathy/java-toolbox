package com.doctusoft.dataops;

import com.doctusoft.annotation.Beta;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Comparator.*;
import static java.util.Objects.*;

public interface Entries<K, V> {
    
    boolean next(BiConsumer<K, V> action);
    
    default void forEach(BiConsumer<K, V> action) {
        while (next(action)) {
            // nothing more to do here
        }
    }
    
    default <M extends Map<? super K, ? super V>> M intoMap(M targetMap) {
        requireNonNull(targetMap);
        forEach((k, v) -> targetMap.put(k, v));
        return targetMap;
    }
    
    default EntryGroups<K, V, ArrayList<V>> intoGroups() {
        return new EntryGroups<>(this, ArrayList::new);
    }
    
    default <G extends Collection<V>> EntryGroups<K, V, G> intoGroups(Supplier<? extends G> groupFactory) {
        return new EntryGroups<>(this, groupFactory);
    }
    
    @Beta
    default <T> Entries<K, T> transformValues(Function<? super V, T> valueTransformer) {
        return new TransformedEntries<>(this, valueTransformer);
    }
    
    static <K, V> Entries<K, V> forMap(Map<K, V> map) {
        return forIterator(map.entrySet().iterator(), EntryAction.identity());
    }
    
    static <K, V> Entries<K, V> forEntries(Iterable<Entry<K, V>> entries) {
        return forIterator(entries.iterator(), EntryAction.identity());
    }
    
    static <K, V> Entries<K, V> forEntryStream(Stream<Entry<K, V>> stream) {
        return forStream(stream, EntryAction.identity());
    }
    
    static <K, V> Entries<K, V> indexValues(Iterable<V> values, Function<? super V, ? extends K> mapper) {
        return indexValueIterator(values.iterator(), mapper);
    }
    
    static <K, V> Entries<K, V> indexValueIterator(Iterator<V> iterator, Function<? super V, ? extends K> mapper) {
        return forIterator(iterator, EntryAction.indexValues(mapper));
    }
    
    static <K, V> Entries<K, V> indexValueStream(Stream<V> stream, Function<? super V, ? extends K> mapper) {
        return forStream(stream, EntryAction.indexValues(mapper));
    }
    
    static <K, V, T> Entries<K, V> forIterator(Iterator<T> iterator, EntryAction<K, V, T> action) {
        return new EntriesForIterator<>(iterator, action);
    }
    
    static <K, V, T> Entries<K, V> forStream(Stream<T> stream, EntryAction<K, V, T> action) {
        return new EntriesForStream<>(stream.spliterator(), action);
    }
    
    static <K extends Comparable<? super K>, V> Entries<K, V> sortAndIndex(
        List<V> values, Function<? super V, ? extends K> mapper) {
        values.sort(comparing(mapper));
        return indexValues(values, mapper);
    }
    
}
