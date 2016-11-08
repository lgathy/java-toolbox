package com.doctusoft.math;

import com.doctusoft.java.AnException;
import org.junit.Test;

import java.util.*;
import java.util.stream.*;

import static com.doctusoft.java.LambdAssert.assertThrows;
import static com.doctusoft.math.ExponentialDelays.ints;
import static com.doctusoft.math.ExponentialDelays.intsFrom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class TestExponentialIntDelays {
    
    @Test
    public void defaultDelays() {
        assertFirstDelays(ints().build(), 1, 2, 4, 8, 16, 32, 64);
    }
    
    @Test
    public void delaysWithInitialValue() {
        assertFirstDelays(intsFrom(3).build(), 3, 6, 12, 24, 48, 96);
        assertThrows(() -> intsFrom(0), AnException.of(IllegalArgumentException.class));
        assertThrows(() -> intsFrom(-1), AnException.of(IllegalArgumentException.class));
        int randomNegative = -1 * new Random().nextInt(Integer.MAX_VALUE);
        assertThrows(() -> intsFrom(randomNegative), AnException.of(IllegalArgumentException.class));
    }
    
    @Test
    public void delaysDoNotOverflow() {
        int mask = 1 << 30;
        int start = mask + new Random().nextInt(mask);
        assertFirstDelays(intsFrom(start).build(), start, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    @Test
    public void limitMaxAttempts() {
        assertAllDelays(ints().limitMaxAttempts(7).build(), 1, 2, 4, 8, 16, 32, 64);
        assertAllDelays(intsFrom(3).limitMaxAttempts(6).build(), 3, 6, 12, 24, 48, 96);
        assertAllDelays(intsFrom(3).limitMaxAttempts(8).limitMaxAttempts(6).build(), 3, 6, 12, 24, 48, 96);
        assertAllDelays(intsFrom(3).limitMaxAttempts(2).limitMaxAttempts(6).build(), 3, 6);
        ExponentialDelays.OfInt original = intsFrom(3).limitMaxAttempts(6);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxAttempts(6));
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxAttempts(7));
    }
    
    @Test
    public void limitMaxDelay() {
        assertFirstDelays(ints().limitMaxDelay(7).build(), 1, 2, 4, 7, 7, 7, 7, 7);
        assertFirstDelays(ints().limitMaxDelay(8).build(), 1, 2, 4, 8, 8, 8, 8, 8);
        assertFirstDelays(intsFrom(3).limitMaxDelay(32).build(), 3, 6, 12, 24, 32, 32, 32, 32);
        assertFirstDelays(intsFrom(3).limitMaxDelay(100).limitMaxDelay(32).build(), 3, 6, 12, 24, 32, 32, 32, 32);
        assertFirstDelays(intsFrom(3).limitMaxDelay(32).limitMaxDelay(100).build(), 3, 6, 12, 24, 32, 32, 32, 32);
        ExponentialDelays.OfInt original = intsFrom(3);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(Integer.MAX_VALUE));
        original = ints().limitMaxDelay(17);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(17));
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(Integer.MAX_VALUE));
    }
    
    @Test
    public void limitMaxIncrements() {
        assertFirstDelays(ints().limitMaxIncrements(5).build(), 1, 2, 4, 8, 16, 32, 32, 32, 32);
        assertThrows(() -> ints().limitMaxIncrements(0), AnException.of(IllegalArgumentException.class));
        assertThrows(() -> ints().limitMaxIncrements(-1), AnException.of(IllegalArgumentException.class));
        ExponentialDelays.OfInt original = ints();
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxIncrements(31));
    }
    
    @Test
    public void limitMaxAttemptsAndMaxIncrements() {
        assertAllDelays(ints().limitMaxAttempts(7).limitMaxIncrements(4).build(), 1, 2, 4, 8, 16, 16, 16);
        assertAllDelays(ints().limitMaxAttempts(4).limitMaxIncrements(7).build(), 1, 2, 4, 8);
        ExponentialDelays.OfInt original = intsFrom(3).limitMaxAttempts(4);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxIncrements(3));
    }
    
    @Test
    public void limitMaxAttemptsAndMaxDelay() {
        assertAllDelays(ints().limitMaxAttempts(7).limitMaxDelay(4).build(), 1, 2, 4, 4, 4, 4, 4);
        assertAllDelays(ints().limitMaxAttempts(7).limitMaxDelay(60).build(), 1, 2, 4, 8, 16, 32, 60);
        assertAllDelays(ints().limitMaxDelay(60).limitMaxAttempts(7).build(), 1, 2, 4, 8, 16, 32, 60);
        assertAllDelays(ints().limitMaxDelay(100).limitMaxAttempts(7).build(), 1, 2, 4, 8, 16, 32, 64);
        assertAllDelays(ints().limitMaxAttempts(7).limitMaxDelay(100).build(), 1, 2, 4, 8, 16, 32, 64);
    }
    
    @Test
    public void limitMaxIncrementsAndMaxDelay() {
        assertFirstDelays(ints().limitMaxIncrements(7).limitMaxDelay(4).build(), 1, 2, 4, 4, 4, 4, 4);
        assertFirstDelays(ints().limitMaxIncrements(7).limitMaxDelay(60).build(), 1, 2, 4, 8, 16, 32, 60);
        assertFirstDelays(ints().limitMaxDelay(60).limitMaxIncrements(7).build(), 1, 2, 4, 8, 16, 32, 60);
        assertFirstDelays(ints().limitMaxDelay(100).limitMaxIncrements(7).build(), 1, 2, 4, 8, 16, 32, 64, 100, 100);
        assertFirstDelays(ints().limitMaxIncrements(7).limitMaxDelay(100).build(), 1, 2, 4, 8, 16, 32, 64, 100, 100);
        ExponentialDelays.OfInt original = intsFrom(5).limitMaxIncrements(4);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(80));
        original = intsFrom(3).limitMaxDelay(95);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxIncrements(5));
    }
    
    private static void assertFirstDelays(IntStream stream, int... delays) {
        assertNextDelays(stream.iterator(), delays);
    }
    
    private static void assertAllDelays(IntStream stream, int... delays) {
        PrimitiveIterator.OfInt iterator = stream.iterator();
        assertNextDelays(iterator, delays);
        if (iterator.hasNext()) {
            fail("More delays follow after " + print(delays) + ": " + iterator.nextInt() + (iterator.hasNext() ? "..." : ""));
        }
    }
    
    private static void assertNextDelays(PrimitiveIterator.OfInt iterator, int... delays) {
        int expectedCount = delays.length;
        for (int i = 0; i < expectedCount; ++i) {
            int expected = delays[i];
            String pre = print(Arrays.copyOf(delays, i));
            if (iterator.hasNext()) {
                assertEquals("After: " + pre, expected, iterator.nextInt());
            } else {
                fail("Expected: " + print(delays) + " but was: " + pre);
            }
        }
    }
    
    private static String print(int... array) {
        int count = array.length;
        if (count == 0) return "[]";
        StringBuilder buf = new StringBuilder(8 * count);
        for (int i = 0; i < count; ++i) {
            buf.append(',').append(array[i]);
        }
        buf.append(']').setCharAt(0, '[');
        return buf.toString();
    }
    
}
