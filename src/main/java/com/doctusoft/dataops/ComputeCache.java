package com.doctusoft.dataops;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

public class ComputeCache<K, V> implements Serializable {
    
    public static final <K extends Comparable<? super K>, V> ComputeCache<K, V> naturalOrder(Function<K, V> computeFun) {
        return new ComputeCache<>(new TreeMap<>(), computeFun);
    }
    
    public static final <K, V> ComputeCache<K, V> preserveInsertionOrder(Function<K, V> computeFun) {
        return new ComputeCache<>(new LinkedHashMap<>(), computeFun);
    }
    
    public static final <K extends Enum<K>, V> ComputeCache<K, V> forEnum(Class<K> enumClass, Function<? super K, V> computeFun) {
        return new ComputeCache<>(new EnumMap<>(enumClass), computeFun);
    }

    private final Map<K, V> map;
    private final Function<? super K, ? extends V> computeFun;
    
    public ComputeCache(Map<K, V> map, Function<? super K, ? extends V> computeFun) {
        this.map = requireNonNull(map, "map");
        this.computeFun = requireNonNull(computeFun, "computeFun");
    }

    public ComputeCache(Function<K, V> computeFun) {
        this(new HashMap<>(), computeFun);
    }
    
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(map);
    }

    public final V get(K key) {
        return map.computeIfAbsent(key, computeFun);
    }

    public <T> T copyValues(Function<Collection<V>, T> copyFun) {
        return copyFun.apply(map.values());
    }
    
}
