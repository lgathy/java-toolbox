package com.doctusoft.math;

import com.doctusoft.annotation.Beta;
import com.doctusoft.java.Failsafe;

import java.math.BigInteger;
import java.util.function.*;

import static com.doctusoft.java.Failsafe.cannotHappen;
import static com.doctusoft.java.Failsafe.checkArgument;
import static com.doctusoft.math.Interval.*;
import static com.doctusoft.math.Interval.monotonicIncreasingValues;
import static com.doctusoft.math.Interval.strictlyMonotonicIncreasingValues;
import static java.util.Objects.*;

@Beta
@SuppressWarnings("rawtypes")
public final class ClosedRange<C extends Comparable> implements Interval<C> {

    public static final <C extends Comparable> ClosedRange<C> create(C lowerBound, C upperBound) {
        requireNonNull(lowerBound, "lowerBound");
        requireNonNull(upperBound, "upperBound");
        checkArgument(monotonicIncreasingValues(lowerBound, upperBound),
            () -> "Invalid interval: " + lowerBound + " > " + upperBound);
        return new ClosedRange<>(lowerBound, upperBound);
    }

    public static final <C extends Comparable> ClosedRange<C> singleValue(C value) {
        requireNonNull(value);
        return new ClosedRange<C>(value, value);
    }

    private final C lowerBound;
    
    private final C upperBound;
    
    private ClosedRange(C lowerBound, C upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public <T extends Comparable> ClosedRange<T> convert(Function<? super C, ? extends T> convertFun) {
        requireNonNull(convertFun, "convertFun");
        return create(
            convertFun.apply(lowerBound),
            convertFun.apply(upperBound));
    }
    
    public C getLowerBound() {
        return lowerBound;
    }
    
    public C getUpperBound() {
        return upperBound;
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public boolean contains(C value) {
        return monotonicIncreasingValues(lowerBound, requireNonNull(value), upperBound);
    }
    
    public boolean isConnected(ClosedRange<C> other) {
        return monotonicIncreasingValues(lowerBound, other.upperBound)
            && monotonicIncreasingValues(other.lowerBound, upperBound);
    }

    public boolean isValidLowerBound(C lowerBound) {
        requireNonNull(lowerBound, "lowerBound");
        return monotonicIncreasingValues(lowerBound, upperBound);
    }

    public boolean isValidUpperBound(C upperBound) {
        requireNonNull(upperBound, "upperBound");
        return monotonicIncreasingValues(lowerBound, upperBound);
    }

    public ClosedRange<C> withLowerBound(C lowerBound) {
        return ClosedRange.create(lowerBound, upperBound);
    }
    
    public ClosedRange<C> withUpperBound(C upperBound) {
        return ClosedRange.create(lowerBound, upperBound);
    }

    @SuppressWarnings("unchecked")
    public ClosedRange<C> intersection(ClosedRange<C> other) {
        int lowerCmp = lowerBound.compareTo(other.lowerBound);
        int upperCmp = upperBound.compareTo(other.upperBound);
        if (lowerCmp >= 0 && upperCmp <= 0) {
            return this;
        } else if (lowerCmp <= 0 && upperCmp >= 0) {
            return other;
        } else {
            return create(
                (lowerCmp >= 0) ? lowerBound : other.lowerBound,
                (upperCmp <= 0) ? upperBound : other.upperBound
            );
        }
    }

    public ClosedRange<C> extendWithValue(C value) {
        if (contains(value)) {
            return this;
        }
        if (strictlyMonotonicIncreasingValues(value, lowerBound)) {
            return withLowerBound(value);
        }
        cannotHappen(monotonicIncreasingValues(value, upperBound), () -> "extendWithValue(" + value + ") failed: " + toString());
        return withUpperBound(value);
    }

    public ClosedRange<C> extendWithRange(ClosedRange<C> other) {
        int lowerCmp = lowerBound.compareTo(other.lowerBound);
        int upperCmp = upperBound.compareTo(other.upperBound);
        if (lowerCmp <= 0 && upperCmp >= 0) {
            return this;
        }
        if (lowerCmp >= 0 && upperCmp <= 0) {
            return other;
        }
        return create(
            (lowerCmp <= 0) ? lowerBound : other.lowerBound,
            (upperCmp >= 0) ? upperBound : other.upperBound
        );
    }
    
    public String toString() {
        return "[" + lowerBound + ", " + upperBound + "]";
    }

    public static int intSizeExact(ClosedRange<Integer> intRange) {
        long length = intRange.getUpperBound().longValue() - intRange.getLowerBound().longValue();
        return Math.toIntExact(length + 1L);
    }

    public static long longSizeExact(ClosedRange<Long> longRange) {
        BigInteger length = BigInteger.valueOf(longRange.getUpperBound())
            .subtract(BigInteger.valueOf(longRange.getLowerBound()));
        return length.subtract(BigInteger.ONE).longValueExact();
    }

}
