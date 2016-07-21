package com.doctusoft.dataops;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

public class TestPromise {
    
    @Test
    public void testResolveChaining() {
        testChaining(originalPromise -> originalPromise.resolve(0), Mockito.only(), Mockito.never());
    }
    
    @Test
    public void testFailureChaining() {
        testChaining(originalPromise -> originalPromise.reject(0f), Mockito.never(), Mockito.only());
    }
    
    @SuppressWarnings("unchecked")
    private void testChaining(Consumer<Promise<Integer, Float>> promiseAction, VerificationMode resultVerificationMode, VerificationMode failureVerificationMode) {
        final Consumer<Integer> resultConsumer1 = Mockito.mock(Consumer.class);
        final Consumer<Long> resultConsumer2 = Mockito.mock(Consumer.class);
        final Consumer<BigInteger> resultConsumer3 = Mockito.mock(Consumer.class);
        
        final Consumer<Float> failureConsumer1 = Mockito.mock(Consumer.class);
        final Consumer<Double> failureConsumer2 = Mockito.mock(Consumer.class);
        final Consumer<BigDecimal> failureConsumer3 = Mockito.mock(Consumer.class);
        
        final Promise<Integer, Float> originalPromise = new Promise<>();
        
        originalPromise
                .then(resultConsumer1).fail(failureConsumer1)
                .then(result -> result.longValue(), failure -> failure.doubleValue())
                .then(resultConsumer2).fail(failureConsumer2)
                .then(result -> BigInteger.valueOf(result), failure -> BigDecimal.valueOf(failure))
                .then(resultConsumer3).fail(failureConsumer3);
        
        promiseAction.accept(originalPromise);
        
        Mockito.verify(resultConsumer1, resultVerificationMode).accept(Mockito.eq(0));
        Mockito.verify(resultConsumer2, resultVerificationMode).accept(Mockito.eq(0L));
        Mockito.verify(resultConsumer3, resultVerificationMode).accept(Mockito.eq(BigInteger.valueOf(0L)));
        
        Mockito.verify(failureConsumer1, failureVerificationMode).accept(Mockito.eq(0f));
        Mockito.verify(failureConsumer2, failureVerificationMode).accept(Mockito.eq(0d));
        Mockito.verify(failureConsumer3, failureVerificationMode).accept(Mockito.eq(BigDecimal.valueOf(0d)));
    }
    
}
