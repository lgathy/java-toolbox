package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

public final class EntryGroups<K, V, G extends Collection<V>> implements Entries<K, G> {
    
    private final Entries<K, V> entries;
    private final Supplier<? extends G> factory;
    private final BiPredicate<K, K> keyEquality;
    
    private Entries<K, V> it;
    
    private G actualGroup;
    private K actualKey;
    
    public EntryGroups(Entries<K, V> entries, Supplier<? extends G> factory) {
        this(entries, factory, Objects::equals);
    }
    
    public EntryGroups(Entries<K, V> entries, Supplier<? extends G> factory, BiPredicate<K, K> keyEquality) {
        this.entries = requireNonNull(entries, "entries");
        this.factory = requireNonNull(factory, "factory");
        this.keyEquality = requireNonNull(keyEquality, "keyEquality");
        this.it = entries;
    }
    
    public boolean next(BiConsumer<K, G> action) {
        requireNonNull(action);
        while (it.next(this::acceptValue)) {
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
    
    private void acceptValue(K key, V value) {
        if (actualKey == null) {
            actualKey = key;
            actualGroup = factory.get();
            addValueToGroup(value);
        } else if (key == actualKey || keyEquality.test(key, actualKey)) {
            addValueToGroup(value);
        } else {
            it = action -> {
                action.accept(key, value);
                it = entries;
                return true;
            };
        }
    }

    private void addValueToGroup(V value) {
        actualGroup.add(value);
    }
    
}
