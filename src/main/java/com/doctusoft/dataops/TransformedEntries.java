package com.doctusoft.dataops;

import com.doctusoft.annotation.Beta;

import java.util.function.*;

@Beta
@SuppressWarnings({ "rawtypes", "unchecked" })
final class TransformedEntries<K, T> implements Entries<K, T> {
    
    private final Entries<K, ?> original;
    private final Function valueTransformer;
    
    TransformedEntries(Entries<K, ?> original, Function valueTransformer) {
        if (original instanceof TransformedEntries) {
            TransformedEntries<K, ?> casted = (TransformedEntries<K, ?>) original;
            this.original = casted.original;
            this.valueTransformer = casted.valueTransformer.andThen(valueTransformer);
        } else {
            this.original = original;
            this.valueTransformer = valueTransformer;
        }
    }

    @Override
    public boolean next(BiConsumer<K, T> action) {
        return original.next((k, v) -> action.accept(k, (T) valueTransformer.apply(v)));
    }
    
}
