package com.doctusoft.dataops;

import com.doctusoft.math.ClosedRange;

import java.util.*;
import java.util.function.*;

import static com.doctusoft.java.Failsafe.checkArgument;
import static java.util.Objects.*;

public final class LookupTable<K, V> {

    public static <K, V> LookupTable<K, V> fromValues(
        Iterable<V> values,
        Function<? super V, Integer> valueOrdinalFun,
        ToIntFunction<? super K> keyOrdinalFun,
        ClosedRange<Integer> validRange) {
        return new LookupTable<>(validRange, keyOrdinalFun, Entries.indexValues(values, valueOrdinalFun));
    }

    public static <K, V> LookupTable<K, V> fromMap(
        Map<K, V> map,
        ToIntFunction<? super K> keyOrdinalFun,
        ClosedRange<Integer> validRange) {
        return new LookupTable<>(validRange, keyOrdinalFun,
            Entries.forMap(map).transformKeys(k -> keyOrdinalFun.applyAsInt(k)));
    }

    public static <K, V> LookupTable<K, V> fromEntries(
        Entries<Integer, V> entries,
        ToIntFunction<? super K> keyOrdinalFun,
        ClosedRange<Integer> validRange) {
        return new LookupTable<>(validRange, keyOrdinalFun, entries);
    }

    private final ClosedRange<Integer> validRange;

    private final Object[] table;

    private final ToIntFunction<? super K> keyOrdinalFun;

    private LookupTable(ClosedRange<Integer> validRange, Object[] table,
        ToIntFunction<? super K> keyOrdinalFun) {
        this.validRange = requireNonNull(validRange, "validRange");
        this.table = requireNonNull(table, "table");
        this.keyOrdinalFun = requireNonNull(keyOrdinalFun, "keyOrdinalFun");
    }

    private LookupTable(ClosedRange<Integer> validRange, ToIntFunction<? super K> keyOrdinalFun,
        Entries<Integer, V> entries) {
        this.validRange = requireNonNull(validRange, "validRange");
        this.keyOrdinalFun = requireNonNull(keyOrdinalFun, "keyOrdinalFun");
        int size = validRange.getUpperBound() + 1;
        checkArgument(size > validRange.getUpperBound(), "Integer overflow");
        this.table = new Object[size];
        requireNonNull(entries, "entries").forEach(this::fill);
    }

    private void fill(Integer index, V value) {
        requireNonNull(index, "index");
        requireNonNull(value, () -> "Null value for key: #" + index);
        if (table[index] != null) {
            throw new IllegalArgumentException("Duplicate value for key: #" + index);
        }
        table[index] = value;
    }

    public V get(int ordinal) {
        validRange.checkValid(ordinal, "index");
        return (V) table[ordinal];
    }

    public Optional<V> find(int ordinal) {
        return Optional.ofNullable(get(ordinal));
    }

    public Optional<V> lookup(K key) {
        requireNonNull(key, "key");
        return find(ordinal(key));
    }

    public V require(K key) {
        return lookup(key).orElseThrow(
            () -> new IllegalArgumentException("No value for key: #" + ordinal(key) + ": " + key)
        );
    }

    private int ordinal(K key) {
        return keyOrdinalFun.applyAsInt(key);
    }

    public <T> LookupTable<T, V> changeKeys(ToIntFunction<? super T> newKeyOrdinalFun) {
        return new LookupTable<>(validRange, table, newKeyOrdinalFun);
    }

    public <T> LookupTable<K, T> transformValues(Function<? super V, ? extends T> transformFun) {
        Object[] transformedValues = table.clone();
        int ln = transformedValues.length;
        for (int i = 0; i < ln; ++i) {
            Object original = table[i];
            transformedValues[i] = original == null ? null : transformFun.apply((V) original);
        }
        return new LookupTable<K, T>(validRange, transformedValues, keyOrdinalFun);
    }

}
