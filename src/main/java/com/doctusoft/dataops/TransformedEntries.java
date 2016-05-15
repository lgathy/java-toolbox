package com.doctusoft.dataops;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import static java.util.Objects.requireNonNull;

final class TransformedEntries<K1, K2, V1, V2> extends Entries<K2, V2> {

    private final Entries<K1, V1> entries;
    private final Function<? super K1, ? extends K2> keyFun;
    private final Function<? super V1, ? extends V2> valueFun;

    TransformedEntries(Entries<K1, V1> entries, Function<? super K1, ? extends K2> keyFun,
        Function<? super V1, ? extends V2> valueFun) {
        this.entries = requireNonNull(entries, "entries");
        this.keyFun = requireNonNull(keyFun, "keyFun");
        this.valueFun = requireNonNull(valueFun, "valueFun");
    }

    public boolean next(final EntryConsumer<K2, V2> action) {
        return entries.next(new EntryConsumer<K1, V1>() {
            public void accept(K1 key, V1 value) {
                action.accept(keyFun.apply(key), valueFun.apply(value));
            }
        });
    }

    public <T> Entries<T, V2> transformKeys(Function<? super K2, ? extends T> keyFun) {
        return new TransformedEntries<>(entries, transformKeyFunction(keyFun), valueFun);
    }

    public <T> Entries<K2, T> transformValues(Function<? super V2, ? extends T> valueFun) {
        return new TransformedEntries<>(entries, keyFun, transformValueFunction(valueFun));
    }

    @SuppressWarnings("unchecked")
    private <T> Function<? super K1, ? extends T> transformKeyFunction(Function<? super K2, ? extends T> fun) {
        requireNonNull(fun);
        if (keyFun == Functions.identity()) {
            return (Function<? super K1, ? extends T>) fun;
        }
        return Functions.compose(fun, keyFun);
    }

    @SuppressWarnings("unchecked")
    private <T> Function<? super V1, ? extends T> transformValueFunction(Function<? super V2, ? extends T> fun) {
        requireNonNull(fun);
        if (valueFun == Functions.identity()) {
            return (Function<? super V1, ? extends T>) fun;
        }
        return Functions.compose(fun, valueFun);
    }

}
