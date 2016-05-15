package com.doctusoft.dataops;

import com.google.common.base.Function;

import java.util.*;

import static java.util.Objects.*;

final class EntriesForValues<K, V> extends Entries<K, V> {

    private final Iterator<V> valueIterator;
    private final Function<? super V, ? extends K> mapperFun;

    EntriesForValues(Iterator<V> valueIterator, Function<? super V, ? extends K> mapperFun) {
        this.valueIterator = requireNonNull(valueIterator);
        this.mapperFun = requireNonNull(mapperFun);
    }

    public boolean next(EntryConsumer<K, V> action) {
        if (valueIterator.hasNext()) {
            V value = valueIterator.next();
            action.accept(mapperFun.apply(value), value);
            return true;
        }
        return false;
    }

}
