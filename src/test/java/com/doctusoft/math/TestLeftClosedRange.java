package com.doctusoft.math;

import com.doctusoft.java.AnException;
import com.doctusoft.java.LambdAssert;
import org.junit.Test;

import java.util.concurrent.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class TestLeftClosedRange {
    
    @Test
    public void noEmptyRange() {
        LambdAssert.assertThrows(() -> LeftClosedRange.create(5, 5), AnException.of(IllegalArgumentException.class));
    }
    
    @Test
    public void lowerBoundIsInclusive() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int lowerBound = random.nextInt(1000);
        int upperBound = random.nextInt(lowerBound, 2000);
        LeftClosedRange<Integer> lcRange = LeftClosedRange.create(lowerBound, upperBound);
        assertTrue("lowerBound should be inclusive", lcRange.contains(lowerBound));
    }
    
    @Test
    public void upperBoundIsExclusive() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int lowerBound = random.nextInt(1000);
        int upperBound = random.nextInt(lowerBound, 2000);
        LeftClosedRange<Integer> lcRange = LeftClosedRange.create(lowerBound, upperBound);
        assertFalse("upperBound should be exclusive", lcRange.contains(upperBound));
    }
    
    @Test
    public void assertEqualsMethod() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int lowerBound = random.nextInt(1000);
        int upperBound = random.nextInt(lowerBound, 2000);
        assertEquals(LeftClosedRange.create(lowerBound, upperBound), LeftClosedRange.create(lowerBound, upperBound));
    }
    
    @Test
    public void noIntersection() {
        LeftClosedRange<Integer> range1 = LeftClosedRange.create(1, 5);
        LeftClosedRange<Integer> range2 = LeftClosedRange.create(5, 19);
        LambdAssert.assertThrows(() -> range1.intersection(range2), AnException.of(IllegalArgumentException.class));
        LeftClosedRange<Integer> range3 = LeftClosedRange.create(4, 9);
        assertThat(
            LambdAssert.assertComputes("LeftClosedRange.intersection()", () -> range1.intersection(range3)),
            equalTo(LeftClosedRange.create(4, 5)));
    }
    
    @Test
    public void parse() {
        LeftClosedRange<String> range = LeftClosedRange.parse("[A; B)");
        assertNotNull(range);
        assertThat(range.getLowerBound(), equalTo("A"));
        assertThat(range.getUpperBound(), equalTo("B"));
    }
    
}
