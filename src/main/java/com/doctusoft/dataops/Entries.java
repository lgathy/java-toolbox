package com.doctusoft.dataops;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import java.util.*;
import java.util.Map.*;

import static java.util.Objects.*;

public abstract class Entries<K, V> {
    
    public abstract boolean next(EntryConsumer<K, V> action);
    
    public static final <K, V> Entries<K, V> forMap(Map<K, V> map) {
        return new EntriesForIterator<>(map.entrySet().iterator());
    }

    public static final <K, V> Entries<K, V> forEntries(Iterable<Entry<K, V>> entries) {
        return new EntriesForIterator<>(entries.iterator());
    }

    public static <K, V> Entries<K, V> forIterator(Iterator<Entry<K, V>> iterator) {
        return new EntriesForIterator<>(iterator);
    }

    public static final <K, V> Entries<K, V> indexValues(Iterable<V> values,
        Function<? super V, ? extends K> mapperFun) {
        return new EntriesForValues<>(values.iterator(), mapperFun);
    }

    public static final <K, V> Entries<K, V> indexValueIterator(Iterator<V> iterator,
        Function<? super V, ? extends K> mapperFun) {
        return new EntriesForValues<>(iterator, mapperFun);
    }

    public static final <K, V> Entries<K, V> lookupKeys(Iterable<K> keys, Function<? super K, ? extends V> lookupFun) {
        return new EntriesForKeys<>(keys.iterator(), lookupFun);
    }

    public static final <K, V> Entries<K, V> lookupKeyIterator(Iterator<K> iterator,
        Function<? super K, ? extends V> lookupFun) {
        return new EntriesForKeys<>(iterator, lookupFun);
    }

    public static final <K extends Comparable<? super K>, V> Entries<K, V> sortAndIndex(List<V> values,
        Function<? super V, ? extends K> mapperFun) {
        Collections.sort(values, Ordering.natural().onResultOf(mapperFun));
        return indexValues(values, mapperFun);
    }

    public void forEach(EntryConsumer<K, V> action) {
        while (next(action)) {
            // nothing more to do here
        }
    }
    
    public Entries<K, V> filterKeys(Predicate<? super K> filter) {
        requireNonNull(filter);
        return new FilteredEntries<>(this, filter, Predicates.alwaysTrue());
    }

    public Entries<K, V> filterValues(Predicate<? super V> filter) {
        requireNonNull(filter);
        return new FilteredEntries<>(this, Predicates.alwaysTrue(), filter);
    }

    public <T> Entries<T, V> transformKeys(Function<? super K, ? extends T> keyFun) {
        requireNonNull(keyFun);
        return new TransformedEntries<>(this, keyFun, Functions.<V>identity());
    }

    public <T> Entries<K, T> transformValues(Function<? super V, ? extends T> valueFun) {
        requireNonNull(valueFun);
        return new TransformedEntries<>(this, Functions.<K>identity(), valueFun);
    }

    public ImmutableList<Entry<K, V>> toList() {
        final ImmutableList.Builder<Entry<K, V>> builder = ImmutableList.builder();
        final class AddToAction implements EntryConsumer<K, V> {
            public void accept(K key, V value) {
                builder.add(Maps.immutableEntry(key, value));
            }
        }
        forEach(new AddToAction());
        return builder.build();
    }

    public <T extends List<Entry<K, V>>> T intoList(final T targetList) {
        requireNonNull(targetList);
        final class IntoListAction implements EntryConsumer<K, V> {
            public void accept(K key, V value) {
                targetList.add(Maps.immutableEntry(key, value));
            }
        }
        forEach(new IntoListAction());
        return targetList;
    }

    public <M extends Map<? super K, ? super V>> M intoMap(final M targetMap) {
        requireNonNull(targetMap);
        final class IntoMapAciton implements EntryConsumer<K, V> {
            public void accept(K key, V value) {
                targetMap.put(key, value);
            }
        }
        forEach(new IntoMapAciton());
        return targetMap;
    }

    public EntryGroups<K, V, ArrayList<V>> intoGroups() {
        return new EntryGroups<>(this, new Supplier<ArrayList<V>>() {
            public ArrayList<V> get() {
                return new ArrayList<>();
            }
        });
    }
    
    public <G extends Collection<V>> EntryGroups<K, V, G> intoGroups(Supplier<? extends G> groupFactory) {
        return new EntryGroups<>(this, groupFactory);
    }

}
