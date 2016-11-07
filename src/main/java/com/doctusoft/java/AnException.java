package com.doctusoft.java;

import java.util.Arrays;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for creating {@link Predicate<Exception>} instances for common use-cases (e.G. check exception type or
 * the content of the message).
 */
public final class AnException {
    
    private AnException() {
        throw Failsafe.staticClassInstantiated();
    }
    
    /**
     * Exception instance's type is the given {@code exceptionClass} or one of its subclasses.
     */
    public static Predicate<Exception> of(Class<? extends Exception> exceptionClass) {
        requireNonNull(exceptionClass);
        return exceptionClass::isInstance;
    }
    
    /**
     * Exception instance's type is exactly the given {@code exceptionClass}.
     */
    public static Predicate<Exception> classEquals(Class<? extends Exception> exceptionClass) {
        requireNonNull(exceptionClass);
        return e -> e.getClass().equals(exceptionClass);
    }
    
    /**
     * Exception's {@link Exception#getMessage() message} contains all the provided {@code parts} in a case insensitive 
     * manner.
     */
    public static Predicate<Exception> withMessageContains(String... parts) {
        return e -> containsAllPartsCaseInsensitive(e.getMessage(), parts);
    }
    
    private static boolean containsAllPartsCaseInsensitive(String message, String... parts) {
        String lc = message.toLowerCase();
        return Arrays.stream(parts).allMatch(h -> lc.contains(h.toLowerCase()));
    }
    
}
