package com.doctusoft.dataops;

import java.util.function.*;

import static java.util.Objects.requireNonNull;

final class EntriesForArray<E> implements Entries<Integer, E> {

    private final E[] elements;
    private final int ln;
    private int i;

    EntriesForArray(E[] elements, int i, int ln) {
        this.elements = requireNonNull(elements);
        this.ln = ln;
        this.i = i;
    }

    EntriesForArray(E[] elements) {
        this(elements, 0, elements.length);
    }

    public boolean next(BiConsumer<Integer, E> action) {
        if (i < ln) {
            action.accept(i, elements[i]);
            ++i;
            return true;
        }
        return false;
    }

}
