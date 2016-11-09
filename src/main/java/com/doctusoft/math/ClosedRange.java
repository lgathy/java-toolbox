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
    
    /**
     * Parses the {@code input} {@link String} into a {@link ClosedRange} according to its {@link #toString()}
     * representation or throws {@link IllegalArgumentException} if its not possible or the interval is not a valid.
     * The provided parser function is used to parse the individual bounds, any exceptions thrown by it will pass
     * through to the caller of this method unchanged.
     *
     * @param input     the input string to parse
     * @param parserFun function to parse the individual bounds
     * @return the parsed valid closed range instance
     */
    public static final <C extends Comparable> ClosedRange<C> parse(String input, Function<String, C> parserFun) {
        requireNonNull(parserFun);
        parse:
        {
            if (input.isEmpty()) break parse;
            if (input.charAt(0) != OPEN_SYMBOL) break parse;
            int lastCharAt = input.length() - 1;
            if (input.charAt(lastCharAt) != CLOSE_SYMBOL) break parse;
            String[] parts = SEPARATOR.split(input.substring(1, lastCharAt));
            if (parts.length != 2) break parse;
            return ClosedRange.create(parserFun.apply(parts[0]), parserFun.apply(parts[1]));
        }
        throw new IllegalArgumentException("Invalid ClosedRange: " + input);
        
    }
    
    /**
     * Equivalent to {@code parse(input, Function.identity());}. Please note, that validation of the parsed lower and
     * upper bound values are checked by natural {@link String} ordering, so if you need to parse numbers, you should
     * not do it via: {@code ClosedRange.parse(input).convert(Integer::valueOf)} because it could fail on the first
     * parse even if the input contains a valid closed range (e.G. [4; 17]).
     *
     * @param input the input string to parse
     * @return the parsed valid closed range of {@link String} bounds
     * @see #parse(String, Function)
     */
    public static final ClosedRange<String> parse(String input) {
        return parse(input, Function.identity());
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
        return new StringBuilder().append(OPEN_SYMBOL).append(lowerBound + SEPARATOR + upperBound).append(CLOSE_SYMBOL).toString();
    }
    
    private static final char OPEN_SYMBOL = '[';
    
    private static final String SEPARATOR = "; ";
    
    private static final char CLOSE_SYMBOL = ']';
    
    public static final BigDecimal decimalLengthOf(ClosedRange<BigDecimal> decimalRange) {
        return decimalRange.getUpperBound()
            .subtract(decimalRange.getLowerBound());
    }
    
    /**
     * Computes the discrete element count of the provided {@code ClosedRange<BigInteger>} interval.
     */
    public static final BigInteger countBigints(ClosedRange<BigInteger> bigintRange) {
        return bigintRange.getUpperBound()
            .subtract(bigintRange.getLowerBound())
            .add(BigInteger.ONE);
    }
    
    /**
     * Computes the discrete element count of the provided {@code ClosedRange<Long>} interval. The return value is a 
     * {@link BigInteger}, because the element count for valid interval could be larger than {@link Long#MAX_VALUE}, 
     * however the {@link BigInteger} class has a wide range of methods to do the necessary (checked) conversions
     * where needed.
     */
    public static final BigInteger countLongs(ClosedRange<Long> longRange) {
        return countBigints(longRange.convert(BigInteger::valueOf));
    }
    
    /**
     * Computes the discrete element count of the provided {@code ClosedRange<Integer>} interval. The return value is a 
     * {@link BigInteger}, because the element count for valid interval could be larger than {@link Integer#MAX_VALUE}, 
     * however the {@link BigInteger} class has a wide range of methods to do the necessary (checked) conversions
     * where needed. This is also the reason to choose {@link BigInteger} as a return type here over {@link Long}.
     */
    public static final BigInteger countInts(ClosedRange<Integer> intRange) {
        long size = intRange.getUpperBound().longValue() - intRange.getLowerBound().longValue() + 1L;
        return BigInteger.valueOf(size);
    }
    
}
