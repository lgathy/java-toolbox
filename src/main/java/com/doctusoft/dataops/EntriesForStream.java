package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForStream<K, V, T> implements Entries<K, V> {
    
    private final Spliterator<T> spliterator;
    private final EntryAction<K, V, T> action;
    
    EntriesForStream(Spliterator<T> spliterator, EntryAction<K, V, T> action) {
        this.spliterator = requireNonNull(spliterator);
        this.action = requireNonNull(action);
    }
    
    @Override
    public boolean next(BiConsumer<K, V> consumer) {
        return spliterator.tryAdvance(t -> action.perform(t, consumer));
    }
    
}
