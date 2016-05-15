package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForStream<K, V> implements Entries<K, V> {
    
    private final Spliterator<Map.Entry<K, V>> spliterator;

    EntriesForStream(Spliterator<Map.Entry<K, V>> spliterator) {
        this.spliterator = requireNonNull(spliterator);
    }
    
    public boolean next(BiConsumer<K, V> consumer) {
        return spliterator.tryAdvance(e -> consumer.accept(e.getKey(), e.getValue()));
    }
    
}
