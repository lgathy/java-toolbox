package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.requireNonNull;

public class EntriesSpliterator<K, V, T> implements Spliterator<T> {
    
    private final Entries<K, V> entries;
    
    private final BiFunction<K, V, T> mapperFun;
    
    EntriesSpliterator(Entries<K, V> entries, BiFunction<K, V, T> mapperFun) {
        this.entries = requireNonNull(entries);
        this.mapperFun = requireNonNull(mapperFun);
    }

    public boolean tryAdvance(Consumer<? super T> action) {
        return entries.next((k, v) -> action.accept(mapperFun.apply(k, v)));
    }

    public Spliterator<T> trySplit() {
        return null;
    }

    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    public int characteristics() {
        return ORDERED & NONNULL & IMMUTABLE;
    }
    
}
