package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForValueStream<K, V> implements Entries<K, V> {

    private final Spliterator<V> spliterator;
    private final Function<? super V, ? extends K> mapperFun;

    EntriesForValueStream(Spliterator<V> spliterator, Function<? super V, ? extends K> mapperFun) {
        this.spliterator = requireNonNull(spliterator);
        this.mapperFun = requireNonNull(mapperFun);
    }

    public boolean next(BiConsumer<K, V> consumer) {
        return spliterator.tryAdvance(v -> consumer.accept(mapperFun.apply(v), v));
    }
    
}
