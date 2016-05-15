package com.doctusoft.dataops;

import com.google.common.base.Equivalence;
import com.google.common.base.Supplier;

import java.util.*;

import static java.util.Objects.*;

public final class EntryGroups<K, V, G extends Collection<V>> extends Entries<K, G> {
    
    private final Entries<K, V> entries;
    private final Supplier<? extends G> factory;
    private final Equivalence<? super K> keyEquality;
    
    private Entries<K, V> it;
    
    private G actualGroup;
    private K actualKey;
    
    public EntryGroups(Entries<K, V> entries, Supplier<? extends G> factory) {
        this(entries, factory, Equivalence.equals());
    }
    
    public EntryGroups(Entries<K, V> entries, Supplier<? extends G> factory, Equivalence<? super K> keyEquality) {
        this.entries = requireNonNull(entries, "entries");
        this.factory = requireNonNull(factory, "factory");
        this.keyEquality = requireNonNull(keyEquality, "keyEquality");
        this.it = entries;
    }
    
    public boolean next(EntryConsumer<K, G> action) {
        requireNonNull(action);
        while (it.next(acceptValueConsumer)) {
            if (it != entries) {
                break;
            }
        }
        boolean result = actualKey != null;
        if (result) {
            action.accept(actualKey, actualGroup);
            actualGroup = null;
            actualKey = null;
        }
        return result;
    }
    
    private final EntryConsumer<K, V> acceptValueConsumer = new EntryConsumer<K, V>() {

        @Override
        public void accept(final K key, final V value) {
            if (actualKey == null) {
                actualKey = key;
                actualGroup = factory.get();
                addValueToGroup(value);
            } else if (key == actualKey || keyEquality.equivalent(key, actualKey)) {
                addValueToGroup(value);
            } else {
                it = new Entries<K, V>() {

                    @Override
                    public boolean next(EntryConsumer<K, V> action) {
                        action.accept(key, value);
                        it = entries;
                        return true;
                    }

                    @Override
                    public String toString() {
                        return "EntryGroups.NextEntryPrefetched(" + key + "=" + value + ")";
                    }
                };
            }
        }

        @Override
        public String toString() {
            return "EntryGroups.acceptValueConsumer";
        }
    };

    private void addValueToGroup(V value) {
        actualGroup.add(value);
    }
    
}
