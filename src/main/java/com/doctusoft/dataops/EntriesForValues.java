package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForValues<K, V> implements Entries<K, V> {

    private final Iterator<V> valueIterator;
    private final Function<? super V, ? extends K> mapperFun;

    EntriesForValues(Iterator<V> valueIterator, Function<? super V, ? extends K> mapperFun) {
        this.valueIterator = requireNonNull(valueIterator);
        this.mapperFun = requireNonNull(mapperFun);
    }

    @Override
    public boolean next(BiConsumer<K, V> consumer) {
        if (valueIterator.hasNext()) {
            V value = valueIterator.next();
            consumer.accept(mapperFun.apply(value), value);
            return true;
        }
        return false;
    }
    
}
