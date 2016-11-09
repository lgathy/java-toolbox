package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.*;

import static com.doctusoft.java.Failsafe.cannotHappen;
import static com.doctusoft.java.Failsafe.checkArgument;
import static java.util.Objects.*;

/**
 * Specialized {@link Interval} implementations for closed intervals, a range that contains all values greater than or
 * equal to its {@code lowerBound} and less than or equal to its {@code upperBound}. {@link ClosedRange} instances are
 * <b>immutable</b>, thus thread-safe as well.
 *
 * @param <C> the type argument of the interval's domain
 */
@Beta
@SuppressWarnings("rawtypes")
public final class ClosedRange<C extends Comparable> implements Interval<C> {
    
    /**
     * Checks if the given parameters could form a valid {@link ClosedRange}. This method tolerates {@code null} values
     * passed and will return {@code false} if any given parameter is {@null}.
     */
    public static final <C extends Comparable> boolean isValid(C lowerBound, C upperBound) {
        if (lowerBound == null || upperBound == null) return false;
        return Interval.monotonicIncreasingValues(lowerBound, upperBound);
    }
    
    /**
     * @param lowerBound the lower bound of the closed interval to create
     * @param upperBound the upper bound of the closed interval to create
     * @param <C>        the type argument of the interval's domain
     * @return an interval of {@code [lowerBound; upperBound)}
     * @throws IllegalArgumentException if {@code upperBound < lowerBound}
     */
    public static final <C extends Comparable> ClosedRange<C> create(C lowerBound, C upperBound) {
        requireNonNull(lowerBound, "lowerBound");
        requireNonNull(upperBound, "upperBound");
        checkArgument(Interval.monotonicIncreasingValues(lowerBound, upperBound),
            () -> "Invalid ClosedRange: " + lowerBound + " > " + upperBound);
        return new ClosedRange<>(lowerBound, upperBound);
    }
    
    /**
     * @return the interval of {@code [value; value]}
     */
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
    
    /**
     * Creates a new interval based on this intervals bounds transformed by the provided function.
     * <p><b>Important to note</b> that the converter function should ensure that
     * {@code convertFun(lowerBound) <= convertFun(upperBound)}, otherwise the converted interval would be invalid</p>
     * <p>Naming of the methods differs from the familiar {@code map} methods found in {@link java.util.Optional} and
     * {@link java.util.stream.Stream} for a reason: the returned {@link ClosedRange} will always be an eagerly
     * evaluated instance to ensure that only a valid {@link ClosedRange} instance can be returned</p>
     */
    public <T extends Comparable> ClosedRange<T> convert(Function<? super C, ? extends T> convertFun) {
        requireNonNull(convertFun, "convertFun");
        return create(
            convertFun.apply(lowerBound),
            convertFun.apply(upperBound));
    }
    
    /**
     * @return the lower bound of the interval (cannot be null)
     */
    public C getLowerBound() {
        return lowerBound;
    }
    
    /**
     * @return the upper bound of the interval (cannot be null)
     */
    public C getUpperBound() {
        return upperBound;
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public boolean contains(C value) {
        return Interval.monotonicIncreasingValues(lowerBound, requireNonNull(value), upperBound);
    }
    
    /**
     * @return {@code true} if the interval has at least one common contained element with the {@code other} interval
     */
    public boolean isConnected(ClosedRange<C> other) {
        return Interval.monotonicIncreasingValues(lowerBound, other.upperBound)
            && Interval.monotonicIncreasingValues(other.lowerBound, upperBound);
    }
    
    /**
     * @return {@code true} if {@code this} interval is a subset of the {@code other} provided interval
     */
    public boolean isSubsetOf(ClosedRange<C> other) {
        return Interval.monotonicIncreasingValues(other.lowerBound, lowerBound)
            && Interval.monotonicIncreasingValues(upperBound, other.upperBound);
    }
    
    /**
     * Equivalent to {@code other.isSubsetOf(this)}
     *
     * @return {@code true} if {@code this} interval is a superset of the {@code other} provided interval
     */
    public boolean isSupersetOf(ClosedRange<C> other) {
        return other.isSubsetOf(this);
    }
    
    /**
     * Creates a new {@link ClosedRange} with its lower bound replace with the given parameter.
     */
    public ClosedRange<C> withLowerBound(C lowerBound) {
        return ClosedRange.create(lowerBound, upperBound);
    }
    
    /**
     * Creates a new {@link ClosedRange} with its upper bound replace with the given parameter.
     */
    public ClosedRange<C> withUpperBound(C upperBound) {
        return ClosedRange.create(lowerBound, upperBound);
    }
    
    /**
     * @return the intersection of {@code this} interval and the {@code other} interval provided. No new instance will
     * be instantiated if not necessary (if any of the 2 interval is a subset of the other).
     */
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
    
    /**
     * Creates a {@link ClosedRange} which is the smallest superset of the current interval and also contains the
     * provided {@code value}. If {@code contains(value)} was already {@true} then this instance will be returned.
     *
     * @param value the value to extend the current interval with
     * @return the extended interval
     */
    public ClosedRange<C> extendWithValue(C value) {
        if (contains(value)) {
            return this;
        }
        if (Interval.strictlyMonotonicIncreasingValues(value, lowerBound)) {
            return withLowerBound(value);
        }
        cannotHappen(Interval.monotonicIncreasingValues(value, upperBound), () -> "extendWithValue(" + value + ") failed: " + toString());
        return withUpperBound(value);
    }
    
    /**
     * Creates a {@link ClosedRange} which is the smallest superset of the current interval which also contains all
     * elements of the provided {@code other} {@link ClosedRange}. I {@code other.isSubsetOf(this)} was already {@true}
     * then this instance will be returned.
     *
     * @param other the other interval to extend the current one with
     * @return the extended interval
     */
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
    
    /**
     * Important to note that the {@link ClosedRange#toString()} representation follows the mathematical interval
     * notation of {@code [lowerBound; upperBound]}. Using the "; " as separator is important since some number formats
     * may use the ',' in their {@link String} representation. The type of the interval's type argument is responsible
     * for providing its own {@link String} representation with its {@code toString()} method.
     */
    public String toString() {
        return "[" + lowerBound + "; " + upperBound + "]";
    }
    
    public static final BigDecimal decimalLengthOf(ClosedRange<BigDecimal> decimalRange) {
        return decimalRange.getUpperBound()
            .subtract(decimalRange.getLowerBound());
    }
    
    public static final BigInteger countBigints(ClosedRange<BigInteger> bigintRange) {
        return bigintRange.getUpperBound()
            .subtract(bigintRange.getLowerBound())
            .add(BigInteger.ONE);
    }
    
    public static final BigInteger countLongs(ClosedRange<Long> longRange) {
        return countBigints(longRange.convert(BigInteger::valueOf));
    }
    
    public static final BigInteger countInts(ClosedRange<Integer> intRange) {
        long size = intRange.getUpperBound().longValue() - intRange.getLowerBound().longValue() + 1L;
        return BigInteger.valueOf(size);
    }
    
}
