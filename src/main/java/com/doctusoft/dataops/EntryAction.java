package com.doctusoft.dataops;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;

import static java.util.Objects.*;

@FunctionalInterface
public interface EntryAction<K, V, T> {
    
    void perform(T input, BiConsumer<K, V> action);
    
    static <K, V> EntryAction<K, V, Entry<K, V>> identity() {
        return (entry, action) -> action.accept(entry.getKey(), entry.getValue());
    }
    
    static <K, V> EntryAction<K, V, V> indexValues(Function<? super V, ? extends K> mapper) {
        requireNonNull(mapper);
        return (value, action) -> action.accept(mapper.apply(value), value);
    }
    
    static <K, V> EntryAction<K, V, K> lookupValues(Function<? super K, ? extends V> mapper) {
        requireNonNull(mapper);
        return (key, action) -> action.accept(key, mapper.apply(key));
    }
    
    static <K, V> EntryAction<K, V, K> mapsTo(Map<? super K, ? extends V> map) {
        return lookupValues(map::get);
    }
    
}
