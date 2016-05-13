package com.doctusoft.dataops;

import static org.junit.Assert.*;

final class EntriesTester<K, V> {
    
    private final Entries<K, V> entries;
    
    public EntriesTester(Entries<K, V> entries) {
        this.entries = entries;
    }
    
    public EntriesTester<K, V> assertNext(K key, V value) {
        assertTrue(entries.next((k, v) -> {
            assertEquals(key, k);
            assertEquals(value, v);
        }));
        return this;
    }
    
    public EntriesTester<K, V> assertNoMore() {
        assertFalse(entries.next((k, v) -> fail()));
        return this;
    }
    
}
