package com.doctusoft.java;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Assert extends org.junit.Assert {

    Assert() {
        throw Failsafe.staticClassInstantiated();
    }

    public static <T> T assertComputes(Supplier<T> actual) {
        return assertComputes(actual, () -> actual.toString());
    }

    public static <T> T assertComputes(Object context, Supplier<T> actual) {
        return assertComputes(actual, () -> String.valueOf(context));
    }

    public static <T> T assertComputes(Supplier<T> actual, Supplier<String> message) {
        try {
            return actual.get();
        } catch (Exception e) {
            throw new AssertionError(message.get() + " threw unexpected exception", e);
        }
    }

    public static <T> T assertReturns(Supplier<T> actual, Predicate<? super T> expected) {
        return assertReturns(actual, expected, () -> actual.toString());
    }

    public static <T> T assertReturns(Object context, Supplier<T> actual, Predicate<? super T> expected) {
        return assertReturns(actual, expected, () -> String.valueOf(context));
    }

    public static <T> T assertReturns(Supplier<T> actual, Predicate<? super T> expected, Supplier<String> message) {
        T value = assertComputes(actual, message);
        if (!expected.test(value)) {
            throw new AssertionError(message.get() + " returned unexpected value: " + value);
        }
        return value;
    }

    public static void assertRuns(Runnable action) {
        assertRuns(action, () -> action.toString());
    }

    public static void assertRuns(Object context, Runnable action) {
        assertRuns(action, () -> String.valueOf(context));
    }

    public static void assertRuns(Runnable action, Supplier<String> message) {
        try {
            action.run();
        } catch (Exception e) {
            throw new AssertionError(message.get() + " threw unexpected exception", e);
        }
    }

    public static void assertThrows(Runnable action, Predicate<? super Exception> expectedException) {
        assertThrows(action, expectedException, () -> action.toString());
    }

    public static void assertThrows(Object context, Runnable action, Predicate<? super Exception> expectedException) {
        assertThrows(action, expectedException, () -> String.valueOf(context));
    }

    public static void assertThrows(Runnable action, Predicate<? super Exception> expectedException, Supplier<String> message) {
        try {
            action.run();
            throw new AssertionError(message.get() + " completed without exception");
        } catch (Exception e) {
            assertException(e, expectedException, message);
        }
    }

    public static void assertThrows(Supplier<?> actual, Predicate<? super Exception> expectedException) {
        assertThrows(actual, expectedException, () -> actual.toString());
    }

    public static void assertThrows(Object context, Supplier<?> actual, Predicate<? super Exception> expectedException) {
        assertThrows(actual, expectedException, () -> String.valueOf(context));
    }

    public static void assertThrows(Supplier<?> actual, Predicate<? super Exception> expectedException, Supplier<String> message) {
        try {
            Object value = actual.get();
            throw new AssertionError(message.get() + " completed without exception and returned: " + value);
        } catch (Exception e) {
            assertException(e, expectedException, message);
        }
    }

    private static void assertException(Exception actual, Predicate<? super Exception> expected, Supplier<String> message) {
        if (!expected.test(actual)) {
            throw new AssertionError(message.get() + " threw unexpected exception", actual);
        }
    }

}
