package com.doctusoft.dataops;

import java.io.Serializable;

import com.google.common.base.Converter;

public abstract class StatelessConverter<A, B> extends Converter<A, B> implements Serializable {
    
    public boolean equals(Object object) {
        return object != null && object.getClass() == getClass();
    }
    
    public int hashCode() {
        return getClass().hashCode();
    }
    
}
