package com.doctusoft.dataops;

public interface EntryConsumer<K, V> {
    
    void accept(K key, V value);
    
}
