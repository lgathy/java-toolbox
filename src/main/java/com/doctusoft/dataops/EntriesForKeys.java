package com.doctusoft.dataops;

import com.google.common.base.Function;

import java.util.*;

import static java.util.Objects.*;

final class EntriesForKeys<K, V> extends Entries<K, V> {

    private final Iterator<K> keyIterator;
    private final Function<? super K, ? extends V> lookupFun;

    EntriesForKeys(Iterator<K> keyIterator, Function<? super K, ? extends V> lookupFun) {
        this.keyIterator = requireNonNull(keyIterator);
        this.lookupFun = requireNonNull(lookupFun);
    }

    public boolean next(EntryConsumer<K, V> action) {
        if (keyIterator.hasNext()) {
            K key = keyIterator.next();
            action.accept(key, lookupFun.apply(key));
            return true;
        }
        return false;
    }

}
