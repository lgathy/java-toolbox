package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

@Beta
@SuppressWarnings("rawtypes")
public interface Interval<C extends Comparable> {

    boolean isEmpty();

    boolean contains(C value);
    
}
