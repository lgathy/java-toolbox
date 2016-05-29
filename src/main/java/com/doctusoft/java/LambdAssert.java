package com.doctusoft.java;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class LambdAssert {

    private LambdAssert() {
        throw Failsafe.staticClassInstantiated();
    }

    public static <T> T assertComputes(Supplier<T> actual) {
        try {
            return actual.get();
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception thrown", e);
        }
    }

    public static <T> void assertReturns(Supplier<T> actual, Predicate<? super T> expected) {
        T value = assertComputes(actual);
        if (!expected.test(value)) {
            throw new AssertionError("Unexpected value returned: " + value);
        }
    }

    public static void assertRuns(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception thrown", e);
        }
    }

    public static void assertThrows(Runnable action, Predicate<? super Exception> expectedException) {
        try {
            action.run();
            throw new AssertionError("Completed without exception");
        } catch (Exception e) {
            assertException(e, expectedException);
        }
    }

    public static void assertThrows(Supplier<?> actual, Predicate<? super Exception> expectedException) {
        try {
            Object value = actual.get();
            throw new AssertionError("Completed without exception and returned: " + value);
        } catch (Exception e) {
            assertException(e, expectedException);
        }
    }

    private static void assertException(Exception actual, Predicate<? super Exception> expected) {
        if (!expected.test(actual)) {
            throw new AssertionError("Not the expected exception was thrown", actual);
        }
    }

}
