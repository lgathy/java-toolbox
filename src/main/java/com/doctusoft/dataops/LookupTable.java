package com.doctusoft.dataops;

import com.doctusoft.math.ClosedRange;
import com.doctusoft.math.Intervals;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.util.*;

import static com.doctusoft.dataops.Entries.forMap;
import static com.doctusoft.dataops.Entries.indexValues;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.*;

public final class LookupTable<K, V> {

    public static <K, V> LookupTable<K, V> fromValues(
        Iterable<V> values,
        Function<? super V, Integer> valueOrdinalFun,
        Function<? super K, Integer> keyOrdinalFun,
        ClosedRange<Integer> validRange) {
        return new LookupTable<>(validRange, keyOrdinalFun, indexValues(values, valueOrdinalFun));
    }

    public static <K, V> LookupTable<K, V> fromMap(
        Map<K, V> map,
        Function<? super K, Integer> keyOrdinalFun,
        ClosedRange<Integer> validRange) {
        return new LookupTable<>(validRange, keyOrdinalFun, forMap(map).transformKeys(keyOrdinalFun));
    }

    public static <K, V> LookupTable<K, V> fromEntries(
        Entries<Integer, V> entries,
        Function<? super K, Integer> keyOrdinalFun,
        ClosedRange<Integer> validRange) {
        return new LookupTable<>(validRange, keyOrdinalFun, entries);
    }

    private final ClosedRange<Integer> validRange;

    private final Object[] table;

    private final Function<? super K, Integer> keyOrdinalFun;

    private LookupTable(ClosedRange<Integer> validRange, Object[] table,
        Function<? super K, Integer> keyOrdinalFun) {
        this.validRange = requireNonNull(validRange, "validRange");
        this.table = requireNonNull(table, "table");
        this.keyOrdinalFun = requireNonNull(keyOrdinalFun, "keyOrdinalFun");
    }

    private LookupTable(ClosedRange<Integer> validRange, Function<? super K, Integer> keyOrdinalFun,
        Entries<Integer, V> entries) {
        this.validRange = requireNonNull(validRange, "validRange");
        this.keyOrdinalFun = requireNonNull(keyOrdinalFun, "keyOrdinalFun");
        int size = validRange.getUpperBound() + 1;
        checkArgument(size > validRange.getUpperBound(), "Integer overflow");
        this.table = new Object[size];
        class Fill implements EntryConsumer<Integer, V> {
            public void accept(Integer index, V value) {
                requireNonNull(index, "index");
                if (value == null) {
                    throw new NullPointerException("Null value for key: #" + index);
                }
                if (table[index] != null) {
                    throw new IllegalArgumentException("Duplicate value for key: #" + index);
                }
                table[index] = value;
            }
        }
        requireNonNull(entries, "entries").forEach(new Fill());
    }

    public V get(int ordinal) {
        Intervals.mustContain(validRange, ordinal, "index");
        return (V) table[ordinal];
    }

    public Optional<V> find(int ordinal) {
        return Optional.fromNullable(get(ordinal));
    }

    public Optional<V> lookup(K key) {
        requireNonNull(key, "key");
        return find(ordinal(key));
    }

    public V require(K key) {
        Optional<V> optional = lookup(key);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException("No value for key: #" + ordinal(key) + ": " + key);
    }

    private int ordinal(K key) {
        return keyOrdinalFun.apply(key);
    }

    public <T> LookupTable<T, V> changeKeys(Function<? super T, Integer> newKeyOrdinalFun) {
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
