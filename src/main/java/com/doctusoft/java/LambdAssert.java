package com.doctusoft.java;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Provides hard assertion methods for lazy evaluation of lambdas and reports failures.
 * <p>
 * In cases where there is no return value (or it is not needed in the assertion or afterwards) the input lambda
 * argument has to be {@link Runnable}. When a return value is expected and used the input lambda is a 
 * {@link Supplier} or if an exception is expected then a {@link Callable}. 
 * </p><p>
 * In any cases though instances of other functional interfaces can also be used easily by transforming it's invocation
 * to a compatible lambda expression. For instance when having a {@code Supplier<T> supplier} variable:
 * </p>
 * <pre>
 * {@code
 * LambdAssert.assertThrows(() -> supplier.get(), AnException.of(IllegalStateException.class));
 * }
 * </pre>
 */
public final class LambdAssert {
    
    private LambdAssert() {
        throw Failsafe.staticClassInstantiated();
    }
    
    /**
     * Asserts if {@code expression} computes without throwing any exception.
     *
     * @return the value returned by evaluating the expression
     */
    public static <T> T assertComputes(Supplier<T> expression) {
        return assertComputes(expression, () -> expression.toString());
    }
    
    /**
     * Asserts if {@code expression} computes without throwing any exception.
     *
     * @param context    will be printed as assertion context upon failure
     * @param expression the expression to evaluate
     * @return the value returned by evaluating the expression
     */
    public static <T> T assertComputes(Object context, Supplier<T> expression) {
        return assertComputes(expression, () -> String.valueOf(context));
    }
    
    /**
     * Asserts if {@code expression} computes without throwing any exception.
     *
     * @param expression      the expression to evaluate
     * @param messageSupplier will provide the message printed as assertion context upon failure
     * @return the value returned by evaluating the expression
     */
    public static <T> T assertComputes(Supplier<T> expression, Supplier<String> messageSupplier) {
        try {
            return expression.get();
        } catch (Exception e) {
            throw new AssertionError(messageSupplier.get() + " threw unexpected exception", e);
        }
    }
    
    /**
     * Does the same as {@link #assertComputes(Supplier)} & also validates the returned value using the {@code expected}
     * {@link Predicate}.
     *
     * @param actual   will provide the actual value upon evaluation
     * @param expected the predicate for validating the evaluated result
     * @return the value returned by {@code actual.get()}
     */
    public static <T> T assertReturns(Supplier<T> actual, Predicate<? super T> expected) {
        return assertReturns(actual, expected, () -> actual.toString());
    }
    
    /**
     * Does the same as {@link #assertComputes(Supplier)} & also validates the returned value using the {@code expected}
     * {@link Predicate}.
     *
     * @param context  will be printed as assertion context upon failure
     * @param actual   will provide the actual value upon evaluation
     * @param expected the predicate for validating the evaluated result
     * @return the value returned by {@code actual.get()}
     */
    public static <T> T assertReturns(Object context, Supplier<T> actual, Predicate<? super T> expected) {
        return assertReturns(actual, expected, () -> String.valueOf(context));
    }
    
    /**
     * Does the same as {@link #assertComputes(Supplier)} & also validates the returned value using the {@code expected}
     * {@link Predicate}.
     *
     * @param actual          will provide the actual value upon evaluation
     * @param expected        the predicate for validating the evaluated result
     * @param messageSupplier will provide the message printed as assertion context upon failure
     * @return the value returned by {@code actual.get()}
     */
    public static <T> T assertReturns(Supplier<T> actual, Predicate<? super T> expected, Supplier<String> messageSupplier) {
        T value = assertComputes(actual, messageSupplier);
        if (!expected.test(value)) {
            throw new AssertionError(messageSupplier.get() + " returned unexpected value: " + value);
        }
        return value;
    }
    
    /**
     * Similar to {@link #assertComputes(Supplier)} but without a return value.
     *
     * @param action the action to run
     */
    public static void assertRuns(Runnable action) {
        assertRuns(action, () -> action.toString());
    }
    
    /**
     * Similar to {@link #assertComputes(Supplier)} but without a return value.
     *
     * @param context will be printed as assertion context upon failure
     * @param action  the action to run
     */
    public static void assertRuns(Object context, Runnable action) {
        assertRuns(action, () -> String.valueOf(context));
    }
    
    /**
     * Similar to {@link #assertComputes(Supplier)} but without a return value.
     *
     * @param action          the action to run
     * @param messageSupplier will provide the message printed as assertion context upon failure
     */
    public static void assertRuns(Runnable action, Supplier<String> messageSupplier) {
        try {
            action.run();
        } catch (Exception e) {
            throw new AssertionError(messageSupplier.get() + " threw unexpected exception", e);
        }
    }
    
    /**
     * Asserts that running the given {@code action} throws the {@code expectedException}.
     *
     * @param action            the action to run
     * @param expectedException the predicate for validating the exception thrown by the action
     * 
     * @see AnException
     */
    public static void assertThrows(Runnable action, Predicate<? super Exception> expectedException) {
        assertThrows(action, expectedException, () -> action.toString());
    }
    
    /**
     * Asserts that running the given {@code action} throws the {@code expectedException}.
     *
     * @param context           will be printed as assertion context upon failure
     * @param action            the action to run
     * @param expectedException the predicate for validating the exception thrown by the action
     *
     * @see AnException
     */
    public static void assertThrows(Object context, Runnable action, Predicate<? super Exception> expectedException) {
        assertThrows(action, expectedException, () -> String.valueOf(context));
    }
    
    /**
     * Asserts that running the given {@code action} throws the {@code expectedException}.
     *
     * @param action            the action to run
     * @param expectedException the predicate for validating the exception thrown by the action
     * @param messageSupplier   will provide the message printed as assertion context upon failure
     *
     * @see AnException
     */
    public static void assertThrows(Runnable action, Predicate<? super Exception> expectedException, Supplier<String> messageSupplier) {
        try {
            action.run();
            throw new AssertionError(messageSupplier.get() + " completed without exception");
        } catch (Exception e) {
            assertException(e, expectedException, messageSupplier);
        }
    }
    
    /**
     * Asserts that running the given {@code actual.call()} invocation throws the {@code expectedException}.
     *
     * @param actual            the action to run
     * @param expectedException the predicate for validating the exception thrown by the action
     *
     * @see AnException
     */
    public static void assertThrows(Callable<?> actual, Predicate<? super Exception> expectedException) {
        assertThrows(actual, expectedException, () -> actual.toString());
    }
    
    /**
     * Asserts that running the given {@code actual.call()} invocation throws the {@code expectedException}.
     *
     * @param context           will be printed as assertion context upon failure
     * @param actual            the action to run
     * @param expectedException the predicate for validating the exception thrown by the action
     *
     * @see AnException
     */
    public static void assertThrows(Object context, Callable<?> actual, Predicate<? super Exception> expectedException) {
        assertThrows(actual, expectedException, () -> String.valueOf(context));
    }
    
    /**
     * Asserts that running the given {@code actual.call()} invocation throws the {@code expectedException}.
     *
     * @param actual            the action to run
     * @param expectedException the predicate for validating the exception thrown by the action
     * @param messageSupplier   will provide the message printed as assertion context upon failure
     *
     * @see AnException
     */
    public static void assertThrows(Callable<?> actual, Predicate<? super Exception> expectedException, Supplier<String> messageSupplier) {
        try {
            Object value = actual.call();
            throw new AssertionError(messageSupplier.get() + " completed without exception and returned: " + value);
        } catch (Exception e) {
            assertException(e, expectedException, messageSupplier);
        }
    }
    
    private static void assertException(Exception actual, Predicate<? super Exception> expected, Supplier<String> message) {
        if (!expected.test(actual)) {
            throw new AssertionError(message.get() + " threw unexpected exception", actual);
        }
    }
    
}
