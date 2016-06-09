package com.doctusoft.java;

import java.util.*;
import java.util.function.*;

public final class Failsafe {
    
    private Failsafe() {
        throw staticClassInstantiated();
    }
    
    public static UnsupportedOperationException staticClassInstantiated() {
        return new UnsupportedOperationException("Cannot instantiate a static class");
    }
    
    public static UnsupportedOperationException notImplementedMethod() {
        return new UnsupportedOperationException("Not implemented");
    }
    
    public static RuntimeException cannotHappen() {
        return new RuntimeException("Cannot happen");
    }
    
    public static RuntimeException cannotHappen(String message) {
        return new RuntimeException("Cannot happen: " + message);
    }

    public static RuntimeException cannotHappen(Throwable e) {
        return new RuntimeException("Cannot happen: " + e.getMessage(), e);
    }
    
    public static void cannotHappen(boolean impossible) throws RuntimeException {
        if (impossible) {
            throw cannotHappen();
        }
    }
    
    public static void cannotHappen(boolean impossible, Supplier<String> messageSupplier) throws RuntimeException {
        if (impossible) {
            throw cannotHappen(messageSupplier.get());
        }
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void checkArgument(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
    
    public static void checkArgumentEquals(Object argument, Object expected) {
        if (!Objects.equals(argument, expected)) {
            throw new IllegalArgumentException("Arguments are not equal: " + argument + ", " + expected);
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
    
    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
    
    public static void checkState(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }
    
}
