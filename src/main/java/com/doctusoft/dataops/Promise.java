package com.doctusoft.dataops;

import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import static java.util.Objects.requireNonNull;

import static com.google.common.base.Preconditions.checkState;

public final class Promise<R, F> {
    
    private boolean finished;
    
    @Nullable private R result;
    
    @Nullable private F failure;
    
    private final LinkedList<Consumer<R>> resultConsumers = Lists.newLinkedList();
    
    private final LinkedList<Consumer<F>> failureConsumers = Lists.newLinkedList();
    
    public boolean isFinished() {
        return finished;
    }
    
    public boolean isPending() {
        return !finished;
    }
    
    public boolean hasResult() {
        return finished && failure == null;
    }
    
    public @Nullable R getResult() {
        checkState(hasResult(), "Unexpected failure: %s", failure);
        return result;
    }
    
    public boolean isFailed() {
        return finished && failure != null;
    }
    
    public @Nonnull F getFailure() {
        checkState(isFailed(), "Unexpected result: %s", result);
        return failure;
    }
    
    public @Nonnull Promise<R, F> then(@Nonnull Consumer<R> consumer) {
        requireNonNull(consumer);
        if (hasResult()) {
            consumer.accept(result);
        } else if (!finished) {
            resultConsumers.add(consumer);
        }
        return this;
    }
    
    public @Nonnull Promise<R, F> fail(@Nonnull Consumer<F> handler) {
        requireNonNull(handler);
        if (isFailed()) {
            handler.accept(failure);
        } else if (!finished) {
            failureConsumers.add(handler);
        }
        return this;
    }
    
    public void resolve(@Nullable R result) {
        checkState(!finished, "promise already finished");
        setResult(result);
    }
    
    private void setResult(@Nullable R result) {
        finished = true;
        this.result = result;
        for (Consumer<R> consumer : resultConsumers) {
            consumer.accept(result);
        }
    }
    
    public void reject(@Nonnull F failure) {
        requireNonNull(failure, "failure");
        checkState(!finished, "promise already finished");
        setFailure(failure);
    }
    
    private void setFailure(@Nonnull F failure) {
        finished = true;
        this.failure = failure;
        for (Consumer<F> handler : failureConsumers) {
            handler.accept(failure);
        }
    }
    
    public void accept(@Nonnull Outcome<? extends R, ? extends F> outcome) {
        requireNonNull(outcome, "outcome");
        checkState(!finished, "promise already finished");
        if (outcome.isFailure()) {
            setFailure(outcome.getFailure());
        } else {
            setResult(outcome.getResult());
        }
    }
    
    public void acceptIfPending(@Nonnull Outcome<? extends R, ? extends F> outcome) {
        if (isPending()) {
            accept(outcome);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public enum Filter implements Predicate<Promise> {
        
        FINISHED {
            public boolean apply(@Nonnull Promise promise) {
                return promise.isFinished();
            }
        },
        HAS_RESULT {
            public boolean apply(@Nonnull Promise promise) {
                return promise.hasResult();
            }
        },
        FAILED {
            public boolean apply(@Nonnull Promise promise) {
                return promise.isFailed();
            }
        };
        
        public abstract boolean apply(@Nonnull Promise promise);
    }

}
