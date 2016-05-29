package com.doctusoft.java;

import java.util.Arrays;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public final class AnException {

    private AnException() {
        throw Failsafe.staticClassInstantiated();
    }

    public static Predicate<Exception> of(Class<? extends Exception> exceptionClass) {
        requireNonNull(exceptionClass);
        return exceptionClass::isInstance;
    }

    public static Predicate<Exception> classEquals(Class<? extends Exception> exceptionClass) {
        requireNonNull(exceptionClass);
        return e -> e.getClass().equals(exceptionClass);
    }

    public static Predicate<Exception> withMessageContains(String... parts) {
        return e -> containsAllPartsCaseInsensitive(e.getMessage(), parts);
    }

    private static boolean containsAllPartsCaseInsensitive(String message, String... parts) {
        String lc = message.toLowerCase();
        return Arrays.stream(parts).allMatch(h -> lc.contains(h.toLowerCase()));
    }

}
