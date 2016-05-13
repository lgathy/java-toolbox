package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

/**
 * A LookbackFilter accepts input based on the last accepted value.
 */
public abstract class LookbackFilter<T> implements Predicate<T> {
    
    private T lastAccepted;
    
    protected LookbackFilter() {}
    
    protected abstract boolean acceptNext(T lastAccepted, T next);
    
    protected Object[] identity() {
        return new Object[] { lastAccepted };
    }
    
    public final T getLastAccepted() {
        return lastAccepted;
    }
    
    @Override
    public final boolean test(T input) {
        requireNonNull(input);
        if (lastAccepted == null || acceptNext(lastAccepted, input)) {
            lastAccepted = input;
            return true;
        }
        return false;
    }
    
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return Arrays.equals(identity(), ((LookbackFilter<?>) obj).identity());
    }
    
    @Override
    public final int hashCode() {
        return 961 + 31 * getClass().hashCode() + Arrays.hashCode(identity());
    }
    
    public static final <T> LookbackFilter<T> notTheSame() {
        return new NotEquivalent<>((a, b) -> a == b);
    }
    
    public static final <T> LookbackFilter<T> notEquals() {
        return new NotEquivalent<>(Objects::equals);
    }
    
    public static final <T> LookbackFilter<T> notEquivalent(BiPredicate<? super T, ? super T> equivalence) {
        return new NotEquivalent<>(equivalence);
    }
    
    public static final <T extends Comparable<? super T>> LookbackFilter<T> noDuplicates() {
        return noDuplicates(Comparator.naturalOrder());
    }
    
    public static final <T> LookbackFilter<T> noDuplicates(Comparator<? super T> comparator) {
        return new Ordered<>(ValidPredicates.NO_DUPLICATES, comparator);
    }
    
    public static final <T extends Comparable<? super T>> LookbackFilter<T> monotone() {
        return monotone(Comparator.naturalOrder());
    }
    
    public static final <T> LookbackFilter<T> monotone(Comparator<? super T> comparator) {
        return new Ordered<>(ValidPredicates.MONOTONE, comparator);
    }
    
    public static final <T extends Comparable<? super T>> LookbackFilter<T> strictlyMonotone() {
        return strictlyMonotone(Comparator.naturalOrder());
    }
    
    public static final <T> LookbackFilter<T> strictlyMonotone(Comparator<? super T> comparator) {
        return new Ordered<>(ValidPredicates.STRICTLY_MONOTONE, comparator);
    }
    
    private static final class NotEquivalent<T> extends LookbackFilter<T> {
        
        private final BiPredicate<? super T, ? super T> equivalence;
        
        private NotEquivalent(BiPredicate<? super T, ? super T> equivalence) {
            this.equivalence = requireNonNull(equivalence);
        }
        
        @Override
        protected boolean acceptNext(T lastAccepted, T next) {
            return !equivalence.test(lastAccepted, next);
        }
        
        @Override
        protected Object[] identity() {
            return new Object[] { equivalence, getLastAccepted() };
        }
        
        @Override
        public String toString() {
            return "LookbackFilter.notEquivalent(" + equivalence + ")";
        }
        
    }
    
    private static final class Ordered<T> extends LookbackFilter<T> {
        
        private final Predicate<Integer> validPredicate;
        
        private final Comparator<? super T> comparator;
        
        private Ordered(Predicate<Integer> validPredicate, Comparator<? super T> comparator) {
            this.comparator = requireNonNull(comparator);
            this.validPredicate = requireNonNull(validPredicate);
        }
        
        @Override
        protected boolean acceptNext(T lastAccepted, T next) {
            return validPredicate.test(comparator.compare(lastAccepted, next));
        }
        
        @Override
        protected Object[] identity() {
            return new Object[] { validPredicate, comparator, getLastAccepted() };
        }
        
        @Override
        public String toString() {
            return "LookbackFilter.ordered(" + validPredicate + ", " + comparator + ")";
        }
        
    }
    
    private static enum ValidPredicates implements Predicate<Integer> {
        
        NO_DUPLICATES {
            @Override
            public boolean test(Integer compareResult) {
                return compareResult.intValue() != 0;
            }
        },
        
        MONOTONE {
            @Override
            public boolean test(Integer compareResult) {
                return compareResult.intValue() <= 0;
            }
        },
        
        STRICTLY_MONOTONE {
            @Override
            public boolean test(Integer compareResult) {
                return compareResult.intValue() < 0;
            }
        };
    }
    
}
