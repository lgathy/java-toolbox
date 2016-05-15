package com.doctusoft.dataops;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static java.util.Objects.*;

final class FilteredEntries<K, V> extends Entries<K, V> {

    private final Entries<K, V> entries;
    private final Predicate<? super K> keyFilter;
    private final Predicate<? super V> valueFilter;

    FilteredEntries(Entries<K, V> entries, Predicate<? super K> keyFilter,
        Predicate<? super V> valueFilter) {
        this.entries = requireNonNull(entries, "entries");
        this.keyFilter = requireNonNull(keyFilter, "keyFilter");
        this.valueFilter = requireNonNull(valueFilter, "valueFilter");
    }

    public boolean next(final EntryConsumer<K, V> action) {
        final class FilteredConsumer implements EntryConsumer<K, V> {

            private boolean finished;

            public void accept(K key, V value) {
                if (!finished && keyFilter.apply(key) && valueFilter.apply(value)) {
                    action.accept(key, value);
                    finished = true;
                }
            }
        }
        FilteredConsumer filter = new FilteredConsumer();
        while (entries.next(filter) && !filter.finished) {
            // nothing more to do here
        }
        return filter.finished;
    }

    public Entries<K, V> filterKeys(Predicate<? super K> filter) {
        return new FilteredEntries<>(entries, withKeyFilter(filter), valueFilter);
    }

    public Entries<K, V> filterValues(Predicate<? super V> filter) {
        return new FilteredEntries<>(entries, keyFilter, withValueFilter(filter));
    }

    private Predicate<? super K> withKeyFilter(Predicate<? super K> newFilter) {
        if (keyFilter == Predicates.alwaysTrue()) {
            return newFilter;
        }
        if (keyFilter == Predicates.alwaysFalse()) {
            return keyFilter;
        }
        return Predicates.and(keyFilter, newFilter);
    }

    private Predicate<? super V> withValueFilter(Predicate<? super V> newFilter) {
        if (valueFilter == Predicates.alwaysTrue()) {
            return newFilter;
        }
        if (valueFilter == Predicates.alwaysFalse()) {
            return valueFilter;
        }
        return Predicates.and(valueFilter, newFilter);
    }

}
