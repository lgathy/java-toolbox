package com.doctusoft.dataops;

import javax.annotation.Nullable;

public interface Consumer<T> {
    
    void accept(@Nullable T t);
    
}
