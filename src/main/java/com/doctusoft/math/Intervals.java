package com.doctusoft.math;

import com.doctusoft.annotation.Beta;
import com.doctusoft.java.Failsafe;

import static java.util.Objects.requireNonNull;

@Beta
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Intervals {
    
    private Intervals() {
        throw Failsafe.staticClassInstantiated();
    }

    public static <C extends Comparable> void mustContain(Interval<C> interval, C value, String description) {
        requireNonNull(interval, "interval");
        requireNonNull(value, "value");
        if (!interval.contains(value)) {
            throw new IllegalArgumentException("Invalid " + description + ": " + value);
        }
    }

    public static <C extends Comparable> boolean equalValues(C a1, C a2) {
        return a1.compareTo(a2) == 0;
    }
    
    public static <C extends Comparable> boolean monotonicIncreasingValues(C a1, C a2) {
        return a1.compareTo(a2) <= 0;
    }
    
    public static <C extends Comparable> boolean monotonicIncreasingValues(C a1, C a2, C a3) {
        return monotonicIncreasingValues(a1, a2) && monotonicIncreasingValues(a2, a3);
    }
    
    public static <C extends Comparable> boolean strictlyMonotonicIncreasingValues(C a1, C a2) {
        return a1.compareTo(a2) < 0;
    }
    
    public static <C extends Comparable> boolean strictlyMonotonicIncreasingValues(C a1, C a2, C a3) {
        return strictlyMonotonicIncreasingValues(a1, a2) && strictlyMonotonicIncreasingValues(a2, a3);
    }

}
