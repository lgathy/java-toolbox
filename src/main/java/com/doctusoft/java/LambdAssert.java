package com.doctusoft.java;

/**
 * @deprecated use {@link LambdaAssert}
 */
@Deprecated
public final class LambdAssert extends LambdaAssert {
    
    private LambdAssert() {
        throw Failsafe.staticClassInstantiated();
    }
    
}
