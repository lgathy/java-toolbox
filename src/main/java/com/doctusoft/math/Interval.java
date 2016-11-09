package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

import java.io.Serializable;
import java.util.function.*;

import static java.util.Objects.*;

/**
 * Common interface for specific interval implementations of this package. These are not intended as replacement for
 * Guava Range implementation and are incompatible in design. While Guava Range is a full featured comprehensive
 * solution for covering all mathematical aspects of intervals, the implementation of our {@link Interval} interface are
 * covering specific subset of these features on purpose and are recommended for use-cases where those particular
 * limitations are a requirement and knowing it unmistakably from reading the code is more important than using the same
 * implementation everywhere. Moreover, using a more specialized {@link Interval} implementation may result in increased
 * performance in code execution and smaller memory footprint compared to using the comprehensive Guava Range class.
 * 
 * <p>Important design decisions:
 * <ul>
 * <li>The type argument {@code C extends} {@link Comparable} does use a rawtype (instead of declaring
 * {@code Comparable<? super C>} for maximal portability.</li>
 * <li>The {@link Interval} interface does not extend {@link Predicate} by default, since its usage as a predicate is
 * still available with passing the {@code interval::contains} lambda.</li>
 * <li>The default methods {@code checkContains} provide readable argument validation interited by all implementation
 * classes.</li>
 * <li>All implementations should be serializable.</li>
 * </ul></p>
 *
 * @param <C> the type argument of the interval's domain
 * @see ClosedRange
 */
@Beta
@SuppressWarnings("rawtypes")
public interface Interval<C extends Comparable> extends Serializable {
    
    /**
     * @return {@code true} if the interval contains any element
     */
    boolean isEmpty();
    
    /**
     * @param value the value to check
     * @return {@code true} if the provided value is contained by the interval
     */
    boolean contains(C value);
    
    /**
     * Validates if the provided value is contained by the interval and throws {@link IllegalArgumentException} if not.
     *
     * @param value       the value to check
     * @param description the name of the value used only in the message of the raised exception upon failure
     */
    default void checkContains(C value, String description) {
        checkContains(value, () -> "Invalid " + description + ": " + value);
    }
    
    default void checkContains(C value, Supplier<String> messageSupplier) {
        if (!contains(requireNonNull(value))) {
            throw new IllegalArgumentException(messageSupplier.get());
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
