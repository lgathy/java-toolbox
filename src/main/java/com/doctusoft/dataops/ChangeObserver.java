package com.doctusoft.dataops;

import com.doctusoft.annotation.Beta;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

@Beta
public final class ChangeObserver<T> implements Consumer<T> {
    
    public static final <T> ChangeObserver<T> forIdentity(BiConsumer<? super T, ? super T> changeVisitor) {
        requireNonNull(changeVisitor);
        return new ChangeObserver<>((a, b) -> a != b, changeVisitor);
    }
    
    public static final <T> ChangeObserver<T> forValue(BiConsumer<? super T, ? super T> changeVisitor) {
        requireNonNull(changeVisitor);
        return new ChangeObserver<>((a, b) -> !a.equals(b), changeVisitor);
    }
    
    public static final <T> ChangeObserver<T> forProperty(Function<? super T, ?> getter,
        BiConsumer<? super T, ? super T> changeVisitor) {
        requireNonNull(getter);
        requireNonNull(changeVisitor);
        return new ChangeObserver<>((a, b) -> !Objects.equals(getter.apply(a), getter.apply(b)), changeVisitor);
    }
    
    private final BiPredicate<? super T, ? super T> changePredicate;
    
    private final BiConsumer<? super T, ? super T> changeVisitor;
    
    private T lastValue;
    
    private ChangeObserver(BiPredicate<? super T, ? super T> changePredicate,
        BiConsumer<? super T, ? super T> changeVisitor) {
        this.changePredicate = changePredicate;
        this.changeVisitor = changeVisitor;
    }
    
    @Override
    public void accept(T input) {
        requireNonNull(input);
        if (lastValue != null && changePredicate.test(lastValue, input)) {
            changeVisitor.accept(lastValue, input);
        }
        lastValue = input;
    }
    
    public T getLastValue() {
        return lastValue;
    }
    
}
