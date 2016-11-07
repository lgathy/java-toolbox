package com.doctusoft.java;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

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
    
    /**
     * Soft-assertion is recommended for impossible conditions in the code where the actual computation cannot be
     * continued but - compared to an assert - the execution of the whole application is not corrupted. Throwing a
     * uniformed {@link RuntimeException} is recommended in such cases.
     *
     * @return the exception to throw
     */
    public static RuntimeException cannotHappen() {
        return new RuntimeException("Cannot happen");
    }
    
    /**
     * @see #cannotHappen()
     */
    public static RuntimeException cannotHappen(String message) {
        return new RuntimeException("Cannot happen: " + message);
    }
    
    /**
     * @see #cannotHappen()
     */
    public static RuntimeException cannotHappen(Throwable e) {
        return new RuntimeException("Cannot happen: " + e.getMessage(), e);
    }
    
    /**
     * @see #cannotHappen()
     */
    public static void cannotHappen(boolean impossible) throws RuntimeException {
        if (impossible) {
            throw cannotHappen();
        }
    }
    
    /**
     * @see #cannotHappen()
     */
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
    
    /**
     * @see #checkArgumentEquals(String, Object, Object)
     */
    public static void checkArgumentEquals(Object argument, Object expected) {
        checkArgumentEquals("argument", argument, expected);
    }
    
    /**
     * A common checkArgument() use-case is where we need to compare an {@code argument} to an {@code expected} value.
     */
    public static void checkArgumentEquals(String argumentName, Object argument, Object expected) {
        if (!Objects.equals(argument, expected)) {
            throw new IllegalArgumentException("Invalid " + argumentName + ": '" + argument + "' expected: " + expected);
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
    
    /**
     * Converts the given {@code input} and reports parse failure.
     *
     * @see #checkedParse(Object, Function, Supplier)
     */
    public static <T, U> U checkedParse(T input, Function<? super T, ? extends U> parserFun) {
        return checkedParse(input, parserFun, () -> "Failed to parse input: " + String.valueOf(input));
    }
    
    /**
     * Converts the given {@code input} and reports parse failure.
     *
     * @see #checkedParse(Object, Function, Supplier)
     */
    public static <T, U> U checkedParse(T input, Function<? super T, ? extends U> parserFun, String inputName) {
        return checkedParse(input, parserFun, () -> "Failed to parse " + inputName + ": " + String.valueOf(input));
    }
    
    /**
     * Converts the given {@code input} and reports parse failure. The exception message is lazy-evaluated using the
     * given Supplier argument.
     *
     * @param input           the input value
     * @param parserFun       the function to parse the input with, must throw a {@link RuntimeException} upon parse failure
     *                        or return the parsed value otherwise
     * @param messageSupplier lambda Supplier to provide the exception message upon parse failure
     * @param <T>             the input type
     * @param <U>             the parsed type
     * @return the parsed value
     */
    public static <T, U> U checkedParse(T input, Function<? super T, ? extends U> parserFun, Supplier<String> messageSupplier) {
        requireNonNull(parserFun, "parserFun");
        try {
            return parserFun.apply(input);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
    
}
