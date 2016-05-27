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

    public static <T> void assertReturns(Supplier<T> actual, Predicate<T> expected) {
        T value = assertComputes(actual);
        if (!expected.test(value)) {
            throw new AssertionError("Unexpected value returned: " + value);
        }
    }

    public static <T> void assertThrows(Supplier<T> actual, Predicate<Throwable> expectedThrown) {
        try {
            T value = actual.get();
            throw new AssertionError("Completed without exception and returned: " + value);
        } catch (Exception e) {
            if (!expectedThrown.test(e)) {
                throw new AssertionError("Not the expected exception was thrown", e);
            }
        }
    }

}
