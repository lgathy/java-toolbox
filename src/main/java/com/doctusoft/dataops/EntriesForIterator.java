package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForIterator<K, V> implements Entries<K, V> {
    
    private final Iterator<Map.Entry<K, V>> entryIterator;

    EntriesForIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        this.entryIterator = requireNonNull(entryIterator);
    }

    public boolean next(BiConsumer<K, V> consumer) {
        if (entryIterator.hasNext()) {
            Map.Entry<K, V> entry = entryIterator.next();
            consumer.accept(entry.getKey(), entry.getValue());
            return true;
        }
        return false;
    }
    
}
