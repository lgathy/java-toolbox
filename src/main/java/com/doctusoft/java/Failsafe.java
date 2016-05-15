package com.doctusoft.java;

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

    public static RuntimeException cannotHappen(Throwable e) {
        return new RuntimeException("Cannot happen: " + e.getMessage(), e);
    }

    public static void cannotHappen(boolean impossible) throws RuntimeException {
        if (impossible) {
            throw cannotHappen();
        }
    }
    
}
