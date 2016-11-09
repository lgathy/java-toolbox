package com.doctusoft.math;

import com.doctusoft.java.AnException;
import com.doctusoft.java.LambdAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestClosedRange {
    
    @Test
    public void countInts() {
        assertEquals(1, ClosedRange.countInts(ClosedRange.create(0, 0)).intValueExact());
        assertEquals(2, ClosedRange.countInts(ClosedRange.create(0, 1)).intValueExact());
        assertEquals(3, ClosedRange.countInts(ClosedRange.create(0, 2)).intValueExact());
        assertEquals(4, ClosedRange.countInts(ClosedRange.create(0, 3)).intValueExact());
        
        assertEquals(Integer.MAX_VALUE, ClosedRange.countInts(ClosedRange.create(Integer.MIN_VALUE + 2, 0)).intValueExact());
        assertEquals(Integer.MAX_VALUE, ClosedRange.countInts(ClosedRange.create(0, Integer.MAX_VALUE - 1)).intValueExact());
        
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
    public void countLongs() {
        assertEquals(1, ClosedRange.countLongs(ClosedRange.create(0L, 0L)).longValueExact());
        assertEquals(2, ClosedRange.countLongs(ClosedRange.create(0L, 1L)).longValueExact());
        assertEquals(3, ClosedRange.countLongs(ClosedRange.create(0L, 2L)).longValueExact());
        assertEquals(4, ClosedRange.countLongs(ClosedRange.create(0L, 3L)).longValueExact());
        
        assertEquals(Long.MAX_VALUE, ClosedRange.countLongs(ClosedRange.create(Long.MIN_VALUE + 2, 0L)).longValueExact());
        assertEquals(Long.MAX_VALUE, ClosedRange.countLongs(ClosedRange.create(0L, Long.MAX_VALUE - 1)).longValueExact());
        
        assertLongSizeExactArithmeticException(Long.MIN_VALUE, 0);
        assertLongSizeExactArithmeticException(0, Long.MAX_VALUE);
        assertLongSizeExactArithmeticException(Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private void assertLongSizeExactArithmeticException(long lowerBound, long upperBound) {
        LambdAssert.assertThrows(
                () -> ClosedRange.countLongs(ClosedRange.create(lowerBound, upperBound)).longValueExact(),
                e -> e instanceof ArithmeticException);
    }
    
    @Test
    public void assertEqualsMethod() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int lowerBound = random.nextInt(1000);
        int upperBound = random.nextInt(lowerBound, 2000);
        assertEquals(ClosedRange.create(lowerBound, upperBound), ClosedRange.create(lowerBound, upperBound));
    }
    
    @Test
    public void noIntersection() {
        ClosedRange<Integer> range1 = ClosedRange.create(1, 5);
        ClosedRange<Integer> range2 = ClosedRange.create(6, 19);
        LambdAssert.assertThrows(() -> range1.intersection(range2), AnException.of(IllegalArgumentException.class));
        ClosedRange<Integer> range3 = ClosedRange.create(5, 9);
        assertThat(
            LambdAssert.assertComputes("ClosedRange.intersection()", () -> range1.intersection(range3)), 
            Matchers.equalTo(ClosedRange.singleValue(5)));
    }
    
}
