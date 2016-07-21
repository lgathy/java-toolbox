package com.doctusoft.math;

import org.junit.Assert;
import org.junit.Test;
import com.doctusoft.java.LambdaAssert;

public class TestClosedRange {
    
    @Test
    public void testIntSizeExact() {
        Assert.assertEquals(1, ClosedRange.intSizeExact(ClosedRange.create(0, 0)));
        Assert.assertEquals(2, ClosedRange.intSizeExact(ClosedRange.create(0, 1)));
        Assert.assertEquals(3, ClosedRange.intSizeExact(ClosedRange.create(0, 2)));
        Assert.assertEquals(4, ClosedRange.intSizeExact(ClosedRange.create(0, 3)));
        
        Assert.assertEquals(Integer.MAX_VALUE, ClosedRange.intSizeExact(ClosedRange.create(Integer.MIN_VALUE + 2, 0)));
        Assert.assertEquals(Integer.MAX_VALUE, ClosedRange.intSizeExact(ClosedRange.create(0, Integer.MAX_VALUE - 1)));
        
        assertIntSizeExactArithmeticException(Integer.MIN_VALUE, 0);
        assertIntSizeExactArithmeticException(0, Integer.MAX_VALUE);
        assertIntSizeExactArithmeticException(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    private void assertIntSizeExactArithmeticException(int lowerBound, int upperBound) {
        LambdaAssert.assertThrows(
                () -> ClosedRange.intSizeExact(ClosedRange.create(lowerBound, upperBound)),
                e -> e instanceof ArithmeticException);
    }
    
    @Test
    public void testLongSizeExact() {
        Assert.assertEquals(1, ClosedRange.longSizeExact(ClosedRange.create(0L, 0L)));
        Assert.assertEquals(2, ClosedRange.longSizeExact(ClosedRange.create(0L, 1L)));
        Assert.assertEquals(3, ClosedRange.longSizeExact(ClosedRange.create(0L, 2L)));
        Assert.assertEquals(4, ClosedRange.longSizeExact(ClosedRange.create(0L, 3L)));
        
        Assert.assertEquals(Long.MAX_VALUE, ClosedRange.longSizeExact(ClosedRange.create(Long.MIN_VALUE + 2, 0L)));
        Assert.assertEquals(Long.MAX_VALUE, ClosedRange.longSizeExact(ClosedRange.create(0L, Long.MAX_VALUE - 1)));
        
        assertLongSizeExactArithmeticException(Long.MIN_VALUE, 0);
        assertLongSizeExactArithmeticException(0, Long.MAX_VALUE);
        assertLongSizeExactArithmeticException(Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private void assertLongSizeExactArithmeticException(long lowerBound, long upperBound) {
        LambdaAssert.assertThrows(
                () -> ClosedRange.longSizeExact(ClosedRange.create(lowerBound, upperBound)),
                e -> e instanceof ArithmeticException);
    }
    
}
