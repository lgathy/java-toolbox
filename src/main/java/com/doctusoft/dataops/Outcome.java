package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static com.doctusoft.java.Failsafe.checkState;
import static java.util.Objects.*;

public final class Outcome<R, F> {
    
    public static <R, F> Outcome<R, F> resultOf(R result) {
        return new Outcome<>(result, null);
    }
    
    public static <R, F> Outcome<R, F> failure(F failure) {
        return new Outcome<R, F>(null, requireNonNull(failure));
    }
    
    private final R result;
    
    private final F failure;
    
    private Outcome(R result, F failure) {
        this.result = result;
        this.failure = failure;
    }
    
    public boolean hasResult() {
        return failure == null;
    }

    public R getResult() {
        checkState(hasResult(), () -> "Unexpected failure: " + failure);
        return result;
    }

    public Optional<R> result() {
        if (hasResult()) {
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    public void then(Consumer<? super R> action) {
        if (hasResult()) {
            action.accept(result);
        }
    }

    public <T extends Throwable> R orElseThrow(Function<? super F, ? extends T> exceptionSupplier) throws T {
        return result().orElseThrow(() -> exceptionSupplier.apply(failure));
    }

    public boolean isFailure() {
        return failure != null;
    }

    public F getFailure() {
        checkState(isFailure(), () -> "Unexpected result: " + result);
        return failure;
    }

    public Promise<R, F> toPromise() {
        Promise<R, F> promise = new Promise<>();
        promise.accept(this);
        return promise;
    }
    
    public String toString() {
        if (failure == null) {
            return "Outcome{result=" + result + "}";
        } else {
            return "Outcome{failure=" + failure + "}";
        }
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
            public Object apply(Outcome outcome) {
                return outcome.result;
            }
        },
        TO_FAILURE {
            public Object apply(Outcome outcome) {
                return outcome.failure;
            }
        };
        
        public abstract Object apply(Outcome outcome);
    }
    
    public enum Filter implements Predicate<Outcome<?, ?>> {
        
        HAS_RESULT {
            public boolean test(Outcome<?, ?> outcome) {
                return outcome != null && outcome.hasResult();
            }
        },
        IS_FAILURE {
            public boolean test(Outcome<?, ?> outcome) {
                return outcome != null && outcome.isFailure();
            }
        };
        
        public abstract boolean test(Outcome<?, ?> outcome);
    }
    
}
