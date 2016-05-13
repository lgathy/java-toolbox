package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

import static java.util.Objects.*;

@Beta
@SuppressWarnings("rawtypes")
public interface Interval<C extends Comparable> {
    
    boolean isEmpty();
    
    boolean contains(C value);
    
    default void checkValid(C value, String description) {
        if (!contains(requireNonNull(value))) {
            throw new IllegalArgumentException("Invalid " + description + ": " + value);
        }
    }
    
    @SuppressWarnings("unchecked")
    static <C extends Comparable> boolean equalValues(C a1, C a2) {
        return a1.compareTo(a2) == 0;
    }
    
    @SuppressWarnings("unchecked")
    static <C extends Comparable> boolean monotonicIncreasingValues(C a1, C a2) {
        return a1.compareTo(a2) <= 0;
    }
    
    static <C extends Comparable> boolean monotonicIncreasingValues(C a1, C a2, C a3) {
        return monotonicIncreasingValues(a1, a2) && monotonicIncreasingValues(a2, a3);
    }
    
    @SuppressWarnings("unchecked")
    static <C extends Comparable> boolean strictlyMonotonicIncreasingValues(C a1, C a2) {
        return a1.compareTo(a2) < 0;
    }
    
    static <C extends Comparable> boolean strictlyMonotonicIncreasingValues(C a1, C a2, C a3) {
        return strictlyMonotonicIncreasingValues(a1, a2) && strictlyMonotonicIncreasingValues(a2, a3);
    }
    
}
