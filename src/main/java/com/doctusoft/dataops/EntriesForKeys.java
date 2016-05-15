package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

final class EntriesForKeys<K, V> implements Entries<K, V> {

    private final Iterator<K> keyIterator;
    private final Function<? super K, ? extends V> lookupFun;

    EntriesForKeys(Iterator<K> keyIterator, Function<? super K, ? extends V> lookupFun) {
        this.keyIterator = requireNonNull(keyIterator);
        this.lookupFun = requireNonNull(lookupFun);
    }

    public boolean next(BiConsumer<K, V> consumer) {
        if (keyIterator.hasNext()) {
            K key = keyIterator.next();
            consumer.accept(key, lookupFun.apply(key));
            return true;
        }
        return false;
    }
    
}
