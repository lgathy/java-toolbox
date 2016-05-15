package com.doctusoft.dataops;

import java.util.*;

import static java.util.Objects.requireNonNull;

final class EntriesForIterator<K, V> extends Entries<K, V> {

    private final Iterator<Map.Entry<K, V>> entryIterator;

    EntriesForIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        this.entryIterator = requireNonNull(entryIterator);
    }

    @Override
    public boolean next(EntryConsumer<K, V> action) {
        if (entryIterator.hasNext()) {
            Map.Entry<K, V> entry = entryIterator.next();
            action.accept(entry.getKey(), entry.getValue());
            return true;
        }
        return false;
    }

}
