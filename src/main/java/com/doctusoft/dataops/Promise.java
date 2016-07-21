package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import com.doctusoft.java.Failsafe;

import static com.doctusoft.java.Failsafe.checkState;
import static java.util.Objects.*;

public final class Promise<R, F> {
    
    private boolean finished;
    
    private R result;
    
    private F failure;
    
    private final LinkedList<Consumer<R>> resultConsumers = new LinkedList<>();
    
    private final LinkedList<Consumer<F>> failureConsumers = new LinkedList<>();
    
    public boolean isFinished() {
        return finished;
    }
    
    public boolean isPending() {
        return !finished;
    }
    
    public boolean hasResult() {
        return finished && failure == null;
    }
    
    public R getResult() {
        checkState(hasResult(), () -> "Unexpected failure: " + failure);
        return result;
    }
    
    public boolean isFailed() {
        return finished && failure != null;
    }
    
    public F getFailure() {
        checkState(isFailed(), () -> "Unexpected result: " + result);
        return failure;
    }
    
    public Promise<R, F> then(Consumer<R> consumer) {
        requireNonNull(consumer);
        if (hasResult()) {
            consumer.accept(result);
        } else if (!finished) {
            resultConsumers.add(consumer);
        }
        return this;
    }
    
    public <R2, F2> Promise<R2, F2> then(Function<R, R2> resultFunction, Function<F, F2> failureFunction) {
        requireNonNull(resultFunction);
        requireNonNull(failureFunction);
        final Promise<R2, F2> resultPromise = new Promise<R2, F2>();
        if (isFinished()) {
            if (hasResult()) {
                resultPromise.resolve(resultFunction.apply(result));
            } else if (isFailed()) {
                resultPromise.reject(failureFunction.apply(failure));
            } else {
                Failsafe.cannotHappen();
            }
        } else {
            resultConsumers.add(result -> resultPromise.resolve(resultFunction.apply(result)));
            failureConsumers.add(failure -> resultPromise.reject(failureFunction.apply(failure)));
        }
        return resultPromise;
    }
    
    public Promise<R, F> fail(Consumer<F> handler) {
        requireNonNull(handler);
        if (isFailed()) {
            handler.accept(failure);
        } else if (!finished) {
            failureConsumers.add(handler);
        }
        return this;
    }
    
    public void resolve(R result) {
        checkState(!finished, "promise already finished");
        setResult(result);
    }
    
    private void setResult(R result) {
        finished = true;
        this.result = result;
        for (Consumer<R> consumer : resultConsumers) {
            consumer.accept(result);
        }
    }
    
    public void reject(F failure) {
        requireNonNull(failure, "failure");
        checkState(!finished, "promise already finished");
        setFailure(failure);
    }
    
    private void setFailure(F failure) {
        finished = true;
        this.failure = failure;
        for (Consumer<F> handler : failureConsumers) {
            handler.accept(failure);
        }
    }
    
    public void accept(Outcome<? extends R, ? extends F> outcome) {
        requireNonNull(outcome, "outcome");
        checkState(!finished, "promise already finished");
        if (outcome.isFailure()) {
            setFailure(outcome.getFailure());
        } else {
            setResult(outcome.getResult());
        }
    }
    
    public void acceptIfPending(Outcome<? extends R, ? extends F> outcome) {
        if (isPending()) {
            accept(outcome);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public enum Filter implements Predicate<Promise> {
        
        FINISHED {
            public boolean test(Promise promise) {
                return promise.isFinished();
            }
        },
        HAS_RESULT {
            public boolean test(Promise promise) {
                return promise.hasResult();
            }
        },
        FAILED {
            public boolean test(Promise promise) {
                return promise.isFailed();
            }
        };
        
        public abstract boolean test(Promise promise);
    }
    
}
