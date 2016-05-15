package com.doctusoft.dataops;

import static org.junit.Assert.*;

final class EntriesTester<K, V> {
    
    private final Entries<K, V> entries;
    
    public EntriesTester(Entries<K, V> entries) {
        this.entries = entries;
    }
    
    public EntriesTester<K, V> assertNext(final K key, final V value) {
        assertTrue(entries.next(new EntryConsumer<K, V>() {
            public void accept(K k, V v) {
                assertEquals(key, k);
                assertEquals(value, v);
            }
        }));
        return this;
    }

    public EntriesTester<K, V> assertNoMore() {
        assertFalse(entries.next(failure));
        return this;
    }

    private final EntryConsumer<K, V> failure = new EntryConsumer<K, V>() {
        public void accept(K k, V v) {
            fail();
        }
    };
    
}
