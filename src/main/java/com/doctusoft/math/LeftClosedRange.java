package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.*;

import static com.doctusoft.java.Failsafe.checkArgument;
import static java.util.Objects.*;

/**
 * Specialized {@link Interval} implementations for left-closed right-open intervals, a range that contains all values
 * greater than or equal to its {@code lowerBound} and less than its {@code upperBound}.
 * <p><b>Important to note</b> that although the interval is open from the right side, it still has to have an existing
 * {@code upperBound} value, it cannot represent an infinite upper bound. Moreover, it cannot represent an empty
 * interval, e.G. {@code [x; x)}, because it could be ambiguous in several cases, thus {@code lowerBound < upperBound}
 * always stands for a valid {@link LeftClosedRange} instance.</p>
 * <p>{@link LeftClosedRange} instances are <b>immutable</b>, thus thread-safe as well.</p>
 *
 * @param <C> the type argument of the interval's domain
 */
@Beta
@SuppressWarnings("rawtypes")
public class LeftClosedRange<C extends Comparable> implements Interval<C> {
    
    /**
     * Checks if the given parameters could form a valid {@link LeftClosedRange}. This method tolerates {@code null} input values
     * passed and will return {@code false} if any given parameter is {@null}.
     */
    public static final <C extends Comparable> boolean isValid(C lowerBound, C upperBound) {
        if (lowerBound == null || upperBound == null) return false;
        return Interval.strictlyMonotonicIncreasingValues(lowerBound, upperBound);
    }
    
    /**
     * @param lowerBound the lower bound of the left-closed interval to create
     * @param upperBound the upper bound of the left-closed interval to create
     * @param <C>        the type argument of the interval's domain
     * @return an interval of {@code [lowerBound; upperBound)}
     * @throws IllegalArgumentException if {@code upperBound <= lowerBound}
     */
    public static final <C extends Comparable> LeftClosedRange<C> create(C lowerBound, C upperBound) {
        return new LeftClosedRange<>(lowerBound, upperBound);
    }
    
    /**
     * Parses the {@code input} {@link String} into a {@link LeftClosedRange} according to its {@link #toString()}
     * representation or throws {@link IllegalArgumentException} if its not possible or the interval is not a valid.
     * The provided parser function is used to parse the individual bounds, any exceptions thrown by it will pass
     * through to the caller of this method unchanged.
     *
     * @param input     the input string to parse
     * @param parserFun function to parse the individual bounds
     * @return the parsed valid left-closed range instance
     */
    public static final <C extends Comparable> LeftClosedRange<C> parse(String input, Function<String, C> parserFun) {
        requireNonNull(parserFun);
        parse:
        {
            if (input.isEmpty()) break parse;
            if (input.charAt(0) != OPEN_SYMBOL) break parse;
            int lastCharAt = input.length() - 1;
            if (input.charAt(lastCharAt) != CLOSE_SYMBOL) break parse;
            String[] parts = SEPARATOR.split(input.substring(1, lastCharAt));
            if (parts.length != 2) break parse;
            return new LeftClosedRange<>(parserFun.apply(parts[0]), parserFun.apply(parts[1]));
        }
        throw new IllegalArgumentException("Invalid LeftClosedRange: " + input);
        
    }
    
    /**
     * Equivalent to {@code parse(input, Function.identity());}. Please note, that validation of the parsed lower and
     * upper bound values are checked by natural {@link String} ordering, so if you need to parse numbers, you should
     * not do it via: {@code LeftClosedRange.parse(input).convert(Integer::valueOf)} because it could fail on the first
     * parse even if the input contains a valid closed range (e.G. [4; 17)).
     *
     * @param input the input string to parse
     * @return the parsed valid left-closed range of {@link String} bounds
     * @see #parse(String, Function)
     */
    public static final LeftClosedRange<String> parse(String input) {
        return parse(input, Function.identity());
    }
    
    private final C lowerBound;
    
    private final C upperBound;
    
    protected LeftClosedRange(C lowerBound, C upperBound) {
        requireNonNull(lowerBound, "lowerBound");
        requireNonNull(upperBound, "upperBound");
        checkArgument(Interval.strictlyMonotonicIncreasingValues(lowerBound, upperBound),
            () -> "Invalid LeftClosedRange: " + lowerBound + " > " + upperBound);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    /**
     * Creates a new {@link LeftClosedRange} interval based on this intervals bounds transformed by the provided function.
     * <p><b>Important to note</b> that the converter function should ensure that
     * {@code convertFun(lowerBound) < convertFun(upperBound)}, otherwise the converted interval would be invalid</p>
     * <p>Naming of the methods differs from the familiar {@code map} methods found in {@link java.util.Optional} and
     * {@link java.util.stream.Stream} for a reason: the returned {@link LeftClosedRange} will always be an eagerly
     * evaluated instance to ensure that only a valid {@link LeftClosedRange} instance can be returned</p>
     */
    public <T extends Comparable> LeftClosedRange<T> convert(Function<? super C, ? extends T> convertFun) {
        requireNonNull(convertFun, "convertFun");
        return create(
            convertFun.apply(lowerBound),
            convertFun.apply(upperBound));
    }
    
    /**
     * @return the lower bound (inclusive) of the interval (cannot be null)
     */
    public C getLowerBound() {
        return lowerBound;
    }
    
    /**
     * @return the upper bound (exclusive) of the interval (cannot be null)
     */
    public C getUpperBound() {
        return upperBound;
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public boolean contains(C value) {
        return Interval.monotonicIncreasingValues(lowerBound, requireNonNull(value))
            && Interval.strictlyMonotonicIncreasingValues(value, upperBound);
    }
    
    /**
     * @return {@code true} if the interval has at least one common contained element with the {@code other} interval
     */
    public boolean isConnected(LeftClosedRange<C> other) {
        return Interval.strictlyMonotonicIncreasingValues(lowerBound, other.upperBound)
            && Interval.strictlyMonotonicIncreasingValues(other.lowerBound, upperBound);
    }
    
    /**
     * @return {@code true} if {@code this} interval is a subset of the {@code other} provided interval
     */
    public boolean isSubsetOf(LeftClosedRange<C> other) {
        return Interval.monotonicIncreasingValues(other.lowerBound, lowerBound)
            && Interval.monotonicIncreasingValues(upperBound, other.upperBound);
    }
    
    /**
     * Equivalent to {@code other.isSubsetOf(this)}
     *
     * @return {@code true} if {@code this} interval is a superset of the {@code other} provided interval
     */
    public boolean isSupersetOf(LeftClosedRange<C> other) {
        return other.isSubsetOf(this);
    }
    
    /**
     * Creates a new {@link LeftClosedRange} with its lower bound replace with the given parameter.
     *
     * @throws IllegalArgumentException if the resulting interval would be invalid
     */
    public LeftClosedRange<C> withLowerBound(C lowerBound) {
        return LeftClosedRange.create(lowerBound, upperBound);
    }
    
    /**
     * Creates a new {@link LeftClosedRange} with its upper bound replace with the given parameter.
     *
     * @throws IllegalArgumentException if the resulting interval would be invalid
     */
    public LeftClosedRange<C> withUpperBound(C upperBound) {
        return LeftClosedRange.create(lowerBound, upperBound);
    }
    
    /**
     * @return the intersection of {@code this} interval and the {@code other} interval provided. No new instance will
     * be instantiated if not necessary (if any of the 2 interval is a subset of the other).
     */
    @SuppressWarnings("unchecked")
    public LeftClosedRange<C> intersection(LeftClosedRange<C> other) {
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
     * Creates a {@link LeftClosedRange} which is the smallest superset of the current interval which also contains all
     * elements of the provided {@code other} {@link LeftClosedRange}. I {@code other.isSubsetOf(this)} was already {@true}
     * then this instance will be returned.
     *
     * @param other the other interval to extend the current one with
     * @return the extended interval
     */
    public LeftClosedRange<C> extendWithRange(LeftClosedRange<C> other) {
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
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeftClosedRange that = (LeftClosedRange) o;
        if (lowerBound.compareTo(that.lowerBound) != 0) return false;
        return upperBound.compareTo(that.upperBound) == 0;
        
    }
    
    public int hashCode() {
        int result = lowerBound.hashCode();
        result = 31 * result + upperBound.hashCode();
        return result;
    }
    
    /**
     * Important to note that the {@link LeftClosedRange#toString()} representation follows the mathematical interval
     * notation of {@code [lowerBound; upperBound)}. Using the "; " as separator is important since some number formats
     * may use the ',' in their {@link String} representation. The type of the interval's type argument is responsible
     * for providing its own {@link String} representation with its {@code toString()} method.
     */
    public String toString() {
        return new StringBuilder().append(OPEN_SYMBOL).append(lowerBound + SEPARATOR + upperBound).append(CLOSE_SYMBOL).toString();
    }
    
    private static final char OPEN_SYMBOL = '[';
    
    private static final String SEPARATOR = "; ";
    
    private static final char CLOSE_SYMBOL = ')';
    
    @Beta
    public static final BigDecimal decimalLengthOf(LeftClosedRange<BigDecimal> decimalRange) {
        return decimalRange.getUpperBound()
            .subtract(decimalRange.getLowerBound());
    }
    
    /**
     * Computes the discrete element count of the provided {@code LeftClosedRange<BigInteger>} interval.
     */
    public static final BigInteger countBigints(LeftClosedRange<BigInteger> bigintRange) {
        return bigintRange.getUpperBound()
            .subtract(bigintRange.getLowerBound());
    }
    
    /**
     * Computes the discrete element count of the provided {@code LeftClosedRange<Long>} interval. The return value is a
     * {@link BigInteger}, because the element count for valid interval could be larger than {@link Long#MAX_VALUE},
     * furthermore the {@link BigInteger} class has a wide range of methods to do the necessary (checked) conversions
     * where needed.
     */
    public static final BigInteger countLongs(LeftClosedRange<Long> longRange) {
        return countBigints(longRange.convert(BigInteger::valueOf));
    }
    
    /**
     * Computes the discrete element count of the provided {@code LeftClosedRange<Integer>} interval. The return value is a
     * {@link BigInteger}, because the element count for valid interval could be larger than {@link Integer#MAX_VALUE},
     * furthermore the {@link BigInteger} class has a wide range of methods to do the necessary (checked) conversions
     * where needed.
     */
    public static final BigInteger countInts(LeftClosedRange<Integer> intRange) {
        long size = intRange.getUpperBound().longValue() - intRange.getLowerBound().longValue();
        return BigInteger.valueOf(size);
    }
    
    public static class IntElementsSet extends AbstractSet<Integer> {
        
        private final LeftClosedRange<Integer> range;
        private final int size;
        
        public IntElementsSet(LeftClosedRange<Integer> range) {
            this.range = range;
            this.size = LeftClosedRange.countInts(range).intValueExact();
        }
        
        public Iterator<Integer> iterator() {
            class Itr implements Iterator<Integer> {
                
                private Integer nextValue = range.getLowerBound();
                
                public boolean hasNext() {
                    return range.contains(nextValue);
                }
                
                public Integer next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    Integer valueToReturn = nextValue;
                    nextValue = nextValue + 1;
                    return valueToReturn;
                }
            }
            return new Itr();
        }
        
        public int size() { return size; }
        
        public boolean contains(Object o) {
            if (!(o instanceof Integer)) {
                return false;
            }
            return range.contains((Integer) o);
        }
        
        public boolean remove(Object o) { throw new UnsupportedOperationException(); }
        
        public boolean removeIf(Predicate<? super Integer> filter) { throw new UnsupportedOperationException(); }
        
        public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
        
        public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
        
    }
    
    public static class LongElementsSet extends AbstractSet<Long> {
        
        private final LeftClosedRange<Long> range;
        private final int size;
        
        public LongElementsSet(LeftClosedRange<Long> range) {
            this.range = range;
            this.size = LeftClosedRange.countLongs(range).intValueExact();
        }
        
        public Iterator<Long> iterator() {
            class Itr implements Iterator<Long> {
                
                private Long nextValue = range.getLowerBound();
                
                public boolean hasNext() {
                    return range.contains(nextValue);
                }
                
                public Long next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    Long valueToReturn = nextValue;
                    nextValue = nextValue + 1;
                    return valueToReturn;
                }
            }
            return new Itr();
        }
        
        public int size() { return size; }
        
        public boolean contains(Object o) {
            if (!(o instanceof Long)) {
                return false;
            }
            return range.contains((Long) o);
        }
        
        public boolean remove(Object o) { throw new UnsupportedOperationException(); }
        
        public boolean removeIf(Predicate<? super Long> filter) { throw new UnsupportedOperationException(); }
        
        public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
        
        public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
        
    }
    
}
