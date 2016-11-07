package com.doctusoft.java;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.requireNonNull;

/**
 * Provides standardized forms of checking conditions & throwing exceptions for the most common use-cases. See details
 * per method.
 * <p>
 * Guava-style Precondition checkXXX methods are built-in for the purpose of using them in Java8+ codebase without
 * Guava dependency + to provide a Supplier-style exception message declaration. Only <code>checkArgument</code> &
 * <code>checkState</code> are defined, since {@link java.util.Objects} contains 
 * {@link Objects#requireNonNull(Object) requireNonNull}.
 * </p>
 */
public final class Failsafe {
    
    private Failsafe() {
        throw staticClassInstantiated();
    }
    
    /**
     * Utility classes that only provide static helper methods should never be instantiated, which should be enforced
     * by defining a single private no-arg constructor which throws an exception:
     * <pre>
     * {@code 
     * public final class Utility {
     * 
     *     private Utility() {
     *         throw Failsafe.staticClassInstantiated();
     *     }
     * }}
     * </pre>
     * 
     * @return the exception to throw
     */
    public static UnsupportedOperationException staticClassInstantiated() {
        return new UnsupportedOperationException("Cannot instantiate a static class");
    }
    
    /**
     * Similar to {@link #staticClassInstantiated()} a uniform protection is suggested for methods that are not yet or
     * should never be implemented. This makes tracking all these locations in a large codebase very easy, which could
     * come especially handy where temporary unimplemented methods are left in for later to be implemented.
     * 
     * @return the exception to throw
     */
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
    
    /**
     * Guava-style checkArgument to throw an {@link IllegalArgumentException} when the passed-in {@code expression}
     * is {@code false}.
     */
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Guava-style checkArgument to throw an {@link IllegalArgumentException} when the passed-in {@code expression}
     * is {@code false}.
     */
    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Guava-style checkArgument to throw an {@link IllegalArgumentException} when the passed-in {@code expression}
     * is {@code false}. The exception message is lazy-evaluated using the given Supplier argument.
     */
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
    
    /**
     * Guava-style checkState to throw an {@link IllegalStateException} when the passed-in {@code expression}
     * is {@code false}.
     */
    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
    
    /**
     * Guava-style checkState to throw an {@link IllegalStateException} when the passed-in {@code expression}
     * is {@code false}.
     */
    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
    
    /**
     * Guava-style checkState to throw an {@link IllegalStateException} when the passed-in {@code expression}
     * is {@code false}. The exception message is lazy-evaluated using the given Supplier argument.
     */
    public static void checkState(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }
    
    public static <T, U> U checkParse(T input, Function<? super T, ? extends U> parserFun) {
        return checkParse(input, parserFun, () -> "Failed to parse input: " + String.valueOf(input));
    }
    
    public static <T, U> U checkParse(T input, Function<? super T, ? extends U> parserFun, String inputName) {
        return checkParse(input, parserFun, () -> "Failed to parse " + inputName + ": " + String.valueOf(input));
    }
    
    public static <T, U> U checkParse(T input, Function<? super T, ? extends U> parserFun, Supplier<String> messageSupplier) {
        requireNonNull(parserFun, "parserFun");
        try {
            return parserFun.apply(input);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
    
}
