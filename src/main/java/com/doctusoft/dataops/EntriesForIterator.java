package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForIterator<K, V, T> implements Entries<K, V> {
    
    private final Iterator<T> iterator;
    private final EntryAction<K, V, T> action;
    
    EntriesForIterator(Iterator<T> iterator, EntryAction<K, V, T> action) {
        this.iterator = requireNonNull(iterator);
        this.action = requireNonNull(action);
    }

    @Override
    public boolean next(BiConsumer<K, V> consumer) {
        if (iterator.hasNext()) {
            action.perform(iterator.next(), consumer);
            return true;
        }
        return false;
    }
    
}
