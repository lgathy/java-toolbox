package com.doctusoft.math;

import org.junit.Assert;
import org.junit.Test;
import com.doctusoft.java.LambdAssert;

public class TestClosedRange {
    
    @Test
    public void testIntSizeExact() {
        Assert.assertEquals(1, ClosedRange.countInts(ClosedRange.create(0, 0)).intValueExact());
        Assert.assertEquals(2, ClosedRange.countInts(ClosedRange.create(0, 1)).intValueExact());
        Assert.assertEquals(3, ClosedRange.countInts(ClosedRange.create(0, 2)).intValueExact());
        Assert.assertEquals(4, ClosedRange.countInts(ClosedRange.create(0, 3)).intValueExact());
        
        Assert.assertEquals(Integer.MAX_VALUE, ClosedRange.countInts(ClosedRange.create(Integer.MIN_VALUE + 2, 0)).intValueExact());
        Assert.assertEquals(Integer.MAX_VALUE, ClosedRange.countInts(ClosedRange.create(0, Integer.MAX_VALUE - 1)).intValueExact());
        
        assertIntSizeExactArithmeticException(Integer.MIN_VALUE, 0);
        assertIntSizeExactArithmeticException(0, Integer.MAX_VALUE);
        assertIntSizeExactArithmeticException(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    private void assertIntSizeExactArithmeticException(int lowerBound, int upperBound) {
        LambdAssert.assertThrows(
                () -> ClosedRange.countInts(ClosedRange.create(lowerBound, upperBound)).intValueExact(),
                e -> e instanceof ArithmeticException);
    }
    
    @Test
    public void testLongSizeExact() {
        Assert.assertEquals(1, ClosedRange.countLongs(ClosedRange.create(0L, 0L)).longValueExact());
        Assert.assertEquals(2, ClosedRange.countLongs(ClosedRange.create(0L, 1L)).longValueExact());
        Assert.assertEquals(3, ClosedRange.countLongs(ClosedRange.create(0L, 2L)).longValueExact());
        Assert.assertEquals(4, ClosedRange.countLongs(ClosedRange.create(0L, 3L)).longValueExact());
        
        Assert.assertEquals(Long.MAX_VALUE, ClosedRange.countLongs(ClosedRange.create(Long.MIN_VALUE + 2, 0L)).longValueExact());
        Assert.assertEquals(Long.MAX_VALUE, ClosedRange.countLongs(ClosedRange.create(0L, Long.MAX_VALUE - 1)).longValueExact());
        
        assertLongSizeExactArithmeticException(Long.MIN_VALUE, 0);
        assertLongSizeExactArithmeticException(0, Long.MAX_VALUE);
        assertLongSizeExactArithmeticException(Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private void assertLongSizeExactArithmeticException(long lowerBound, long upperBound) {
        LambdAssert.assertThrows(
                () -> ClosedRange.countLongs(ClosedRange.create(lowerBound, upperBound)).longValueExact(),
                e -> e instanceof ArithmeticException);
    }
    
}
