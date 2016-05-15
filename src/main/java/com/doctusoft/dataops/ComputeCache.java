package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

public class ComputeCache<K, V> {

    private final Map<K, V> map;
    private final Function<? super K, ? extends V> computeFun;

    private ComputeCache(Map<K, V> map, Function<? super K, ? extends V> computeFun) {
        this.map = requireNonNull(map, "map");
        this.computeFun = requireNonNull(computeFun, "computeFun");
    }

    public ComputeCache(Function<K, V> computeFun) {
        this(new HashMap<>(), computeFun);
    }

    public ComputeCache<K, V> preserveInsertionOrder() {
        return new ComputeCache<>(new LinkedHashMap<>(map), computeFun);
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
