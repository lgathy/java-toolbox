package com.doctusoft.dataops;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Comparator.*;
import static java.util.Objects.*;

public interface Entries<K, V> {
    
    boolean next(BiConsumer<K, V> action);
    
    static <K, V> Entries<K, V> forMap(Map<K, V> map) {
        return new EntriesForIterator<>(map.entrySet().iterator());
    }

    static <K, V> Entries<K, V> forEntries(Iterable<Entry<K, V>> entries) {
        return new EntriesForIterator<>(entries.iterator());
    }

    static <K, V> Entries<K, V> forIterator(Iterator<Entry<K, V>> iterator) {
        return new EntriesForIterator<>(iterator);
    }

    static <K, V> Entries<K, V> forEntryStream(Stream<Entry<K, V>> stream) {
        return new EntriesForStream<>(stream.spliterator());
    }

    static <K, V> Entries<K, V> indexValues(Iterable<V> values, Function<? super V, ? extends K> mapperFun) {
        return new EntriesForValues<>(values.iterator(), mapperFun);
    }

    static <K, V> Entries<K, V> indexValueIterator(Iterator<V> iterator, Function<? super V, ? extends K> mapperFun) {
        return new EntriesForValues<>(iterator, mapperFun);
    }

    static <K, V> Entries<K, V> indexValueStream(Stream<V> stream, Function<? super V, ? extends K> mapperFun) {
        return new EntriesForValueStream<>(stream.spliterator(), mapperFun);
    }

    static <K, V> Entries<K, V> lookupKeys(Iterable<K> keys, Function<? super K, ? extends V> lookupFun) {
        return new EntriesForKeys<>(keys.iterator(), lookupFun);
    }

    static <K, V> Entries<K, V> lookupKeyIterator(Iterator<K> iterator, Function<? super K, ? extends V> lookupFun) {
        return new EntriesForKeys<>(iterator, lookupFun);
    }

    static <K, V> Entries<K, V> lookupKeyStream(Stream<K> keys, Function<? super K, ? extends V> lookupFun) {
        return new EntriesForKeyStream<>(keys.spliterator(), lookupFun);
    }

    static <K extends Comparable<? super K>, V> Entries<K, V> sortAndIndex(List<V> values,
        Function<? super V, ? extends K> mapperFun) {
        values.sort(comparing(mapperFun));
        return indexValues(values, mapperFun);
    }

    static <V> Entries<Integer, V> indexedArrayElements(V[] elements) {
        return new EntriesForArray<>(elements);
    }

    default void forEach(BiConsumer<K, V> action) {
        while (next(action)) {
            // nothing more to do here
        }
    }

    default Entries<K, V> filterKeys(Predicate<? super K> filter) {
        requireNonNull(filter);
        return new FilteredEntries<>(this, filter, FilteredEntries.Always.TRUE);
    }

    default Entries<K, V> filterValues(Predicate<? super V> filter) {
        requireNonNull(filter);
        return new FilteredEntries<>(this, FilteredEntries.Always.TRUE, filter);
    }

    default <T> Entries<T, V> transformKeys(Function<? super K, ? extends T> keyFun) {
        requireNonNull(keyFun);
        return new TransformedEntries<>(this, keyFun, Function.identity());
    }

    default <T> Entries<K, T> transformValues(Function<? super V, ? extends T> valueFun) {
        requireNonNull(valueFun);
        return new TransformedEntries<>(this, Function.identity(), valueFun);
    }

    default List<Entry<K, V>> toList() {
        return intoList(new ArrayList<>());
    }

    default <T extends List<Entry<K, V>>> T intoList(final T targetList) {
        forEach((k, v) -> targetList.add(new AbstractMap.SimpleImmutableEntry<>(k, v)));
        return targetList;
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
    
    default <T> Stream<T> stream(BiFunction<K, V, T> mapperFun) {
        return StreamSupport.stream(new EntriesSpliterator<>(this, mapperFun), false);
    }
    
}
