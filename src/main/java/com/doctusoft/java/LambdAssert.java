package com.doctusoft.java;

/**
 * @deprecated use {@link com.doctusoft.java.Assert}
 */
@Deprecated
public final class LambdAssert extends com.doctusoft.java.Assert {
    
    private LambdAssert() {
        throw Failsafe.staticClassInstantiated();
    }
    
}
