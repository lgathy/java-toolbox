package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForKeyStream<K, V> implements Entries<K, V> {

    private final Spliterator<K> spliterator;
    private final Function<? super K, ? extends V> lookupFun;

    EntriesForKeyStream(Spliterator<K> spliterator, Function<? super K, ? extends V> lookupFun) {
        this.spliterator = requireNonNull(spliterator);
        this.lookupFun = requireNonNull(lookupFun);
    }

    public boolean next(BiConsumer<K, V> consumer) {
        return spliterator.tryAdvance(k -> consumer.accept(k, lookupFun.apply(k)));
    }
    
}
