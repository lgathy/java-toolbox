package com.doctusoft.dataops;

import java.util.function.*;

import static java.util.Objects.*;

final class FilteredEntries<K, V> implements Entries<K, V> {

    private final Entries<K, V> entries;
    private final Predicate<? super K> keyFilter;
    private final Predicate<? super V> valueFilter;

    FilteredEntries(Entries<K, V> entries, Predicate<? super K> keyFilter,
        Predicate<? super V> valueFilter) {
        this.entries = requireNonNull(entries, "entries");
        this.keyFilter = requireNonNull(keyFilter, "keyFilter");
        this.valueFilter = requireNonNull(valueFilter, "valueFilter");
    }

    public boolean next(final BiConsumer<K, V> action) {
        final class FilteredConsumer implements BiConsumer<K, V> {

            private boolean finished;

            public void accept(K key, V value) {
                if (!finished && keyFilter.test(key) && valueFilter.test(value)) {
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
        if (keyFilter == Always.TRUE) {
            return newFilter;
        }
        return k -> keyFilter.test(k) && newFilter.test(k);
    }

    private Predicate<? super V> withValueFilter(Predicate<? super V> newFilter) {
        if (valueFilter == Always.TRUE) {
            return newFilter;
        }
        return v -> valueFilter.test(v) && newFilter.test(v);
    }

    enum Always implements Predicate {

        TRUE(true),
        FALSE(false);

        private final boolean constant;

        private Always(boolean constant) {
            this.constant = constant;
        }

        public boolean test(Object o) {
            return constant;
        }
    }

}
