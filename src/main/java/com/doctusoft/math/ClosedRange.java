package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

import static com.doctusoft.java.Failsafe.checkArgument;
import static com.doctusoft.math.Interval.*;
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
    
    private final C lowerBound;
    
    private final C upperBound;
    
    private ClosedRange(C lowerBound, C upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public C getLowerBound() {
        return lowerBound;
    }
    
    public C getUpperBound() {
        return upperBound;
    }
    
    @Override
    public boolean isEmpty() {
        return equalValues(lowerBound, upperBound);
    }
    
    @Override
    public boolean contains(C value) {
        return monotonicIncreasingValues(lowerBound, requireNonNull(value), upperBound);
    }
    
    public boolean isConnected(ClosedRange<C> other) {
        return monotonicIncreasingValues(lowerBound, other.upperBound)
            && monotonicIncreasingValues(other.lowerBound, upperBound);
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
                (upperCmp <= 0) ? upperBound : other.upperBound);
        }
    }
    
    @Override
    public String toString() {
        return "[" + lowerBound + ", " + upperBound + "]";
    }
    
}
