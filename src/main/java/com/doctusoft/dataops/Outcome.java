package com.doctusoft.dataops;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Predicate;

import static java.util.Objects.requireNonNull;

import static com.google.common.base.Preconditions.checkState;

public final class Outcome<R, F> {
    
    @Nonnull public static <R, F> Outcome<R, F> resultOf(@Nullable R result) {
        return new Outcome<>(result, null);
    }
    
    @Nonnull public static <R, F> Outcome<R, F> failure(@Nonnull F failure) {
        return new Outcome<R, F>(null, requireNonNull(failure));
    }
    
    @Nullable private final R result;
    
    @Nullable private final F failure;
    
    private Outcome(R result, F failure) {
        this.result = result;
        this.failure = failure;
    }
    
    public boolean hasResult() {
        return failure == null;
    }
    
    public @Nullable R getResult() {
        checkState(hasResult(), "Unexpected failure: %s", failure);
        return result;
    }
    
    public boolean isFailure() {
        return failure != null;
    }
    
    public @Nonnull F getFailure() {
        checkState(isFailure(), "Unexpected result: %s", result);
        return failure;
    }
    
    public @Nonnull Promise<R, F> toPromise() {
        Promise<R, F> promise = new Promise<>();
        promise.accept(this);
        return promise;
    }
    
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper(getClass());
        if (failure == null) {
            helper.add("result", result);
        } else {
            helper.add("failure", failure);
        }
        return helper.toString();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Function<Outcome<T, ?>, T> toResult() {
        return (Function) Transform.TO_RESULT;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Function<Outcome<?, T>, T> toFailure() {
        return (Function) Transform.TO_FAILURE;
    }
    
    @SuppressWarnings("rawtypes")
    public enum Transform implements Function<Outcome, Object> {
        
        TO_RESULT {
            public Object apply(@Nonnull Outcome outcome) {
                return outcome.result;
            }
        },
        TO_FAILURE {
            public Object apply(@Nonnull Outcome outcome) {
                return outcome.failure;
            }
        };
        
        public abstract @Nullable Object apply(@Nonnull Outcome outcome);
    }
    
    public enum Filter implements Predicate<Outcome<?, ?>> {
        
        HAS_RESULT {
            public boolean apply(Outcome<?, ?> outcome) {
                return outcome != null && outcome.hasResult();
            }
        },
        IS_FAILURE {
            public boolean apply(Outcome<?, ?> outcome) {
                return outcome != null && outcome.isFailure();
            }
        };
        
        public abstract boolean apply(@Nullable Outcome<?, ?> outcome);
    }
    
}
