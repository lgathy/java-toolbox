package com.doctusoft.math;

import com.doctusoft.java.AnException;
import org.junit.Test;

import java.util.*;
import java.util.stream.*;

import static com.doctusoft.java.LambdAssert.assertThrows;
import static com.doctusoft.math.ExponentialDelays.*;
import static org.junit.Assert.*;

public class TestExponentialLongDelays {
    
    @Test
    public void defaultDelays() {
        assertFirstDelays(longs().build(), 1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L,
            1L << 10, 1L << 11, 1L << 12, 1L << 13, 1L << 14, 1L << 15, 1L << 16, 1L << 17, 1L << 18, 1L << 19,
            1L << 20, 1L << 21, 1L << 22, 1L << 23, 1L << 24, 1L << 25, 1L << 26, 1L << 27, 1L << 28, 1L << 29,
            1L << 30, 1L << 31, 1L << 32, 1L << 33, 1L << 34, 1L << 35, 1L << 36, 1L << 37, 1L << 38, 1L << 39,
            1L << 40, 1L << 41, 1L << 42, 1L << 43, 1L << 44, 1L << 45, 1L << 46, 1L << 47, 1L << 48, 1L << 49,
            1L << 50, 1L << 51, 1L << 52, 1L << 53, 1L << 54, 1L << 55, 1L << 56, 1L << 57, 1L << 58, 1L << 59,
            1L << 60, 1L << 61, 1L << 62, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
    }
    
    @Test
    public void delaysWithInitialValue() {
        assertFirstDelays(longsFrom(3).build(), 3, 6, 12, 24, 48, 96);
        assertThrows(() -> longsFrom(0), AnException.of(IllegalArgumentException.class));
        assertThrows(() -> longsFrom(-1), AnException.of(IllegalArgumentException.class));
        long randomNegative = new Random().longs(Long.MIN_VALUE, 0L).findFirst().getAsLong();
        assertThrows(() -> longsFrom(randomNegative), AnException.of(IllegalArgumentException.class));
    }
    
    @Test
    public void delaysDoNotOverflow() {
        long mask = 1L << 62;
        long start = mask + new Random().longs(1L, mask).findFirst().getAsLong();
        assertFirstDelays(longsFrom(start).build(), start, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
    }
    
    @Test
    public void limitMaxAttempts() {
        assertAllDelays(longs().limitMaxAttempts(7).build(), 1, 2, 4, 8, 16, 32, 64);
        assertAllDelays(longsFrom(3).limitMaxAttempts(6).build(), 3, 6, 12, 24, 48, 96);
        assertAllDelays(longsFrom(3).limitMaxAttempts(8).limitMaxAttempts(6).build(), 3, 6, 12, 24, 48, 96);
        assertAllDelays(longsFrom(3).limitMaxAttempts(2).limitMaxAttempts(6).build(), 3, 6);
        ExponentialDelays.OfLong original = longsFrom(3).limitMaxAttempts(6);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxAttempts(6));
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxAttempts(7));
    }
    
    @Test
    public void limitMaxDelay() {
        assertFirstDelays(longs().limitMaxDelay(7).build(), 1, 2, 4, 7, 7, 7, 7, 7);
        assertFirstDelays(longs().limitMaxDelay(8).build(), 1, 2, 4, 8, 8, 8, 8, 8);
        assertFirstDelays(longsFrom(3).limitMaxDelay(32).build(), 3, 6, 12, 24, 32, 32, 32, 32);
        assertFirstDelays(longsFrom(3).limitMaxDelay(100).limitMaxDelay(32).build(), 3, 6, 12, 24, 32, 32, 32, 32);
        assertFirstDelays(longsFrom(3).limitMaxDelay(32).limitMaxDelay(100).build(), 3, 6, 12, 24, 32, 32, 32, 32);
        ExponentialDelays.OfLong original = longsFrom(3);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(Long.MAX_VALUE));
        original = longs().limitMaxDelay(17);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(17));
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(Long.MAX_VALUE));
    }
    
    @Test
    public void limitMaxIncrements() {
        assertFirstDelays(longs().limitMaxIncrements(5).build(), 1, 2, 4, 8, 16, 32, 32, 32, 32);
        assertThrows(() -> longs().limitMaxIncrements(0), AnException.of(IllegalArgumentException.class));
        assertThrows(() -> longs().limitMaxIncrements(-1), AnException.of(IllegalArgumentException.class));
        ExponentialDelays.OfLong original = longs();
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxIncrements(63));
    }
    
    @Test
    public void limitMaxAttemptsAndMaxIncrements() {
        assertAllDelays(longs().limitMaxAttempts(7).limitMaxIncrements(4).build(), 1, 2, 4, 8, 16, 16, 16);
        assertAllDelays(longs().limitMaxAttempts(4).limitMaxIncrements(7).build(), 1, 2, 4, 8);
        ExponentialDelays.OfLong original = longsFrom(3).limitMaxAttempts(4);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxIncrements(3));
    }
    
    @Test
    public void limitMaxAttemptsAndMaxDelay() {
        assertAllDelays(longs().limitMaxAttempts(7).limitMaxDelay(4).build(), 1, 2, 4, 4, 4, 4, 4);
        assertAllDelays(longs().limitMaxAttempts(7).limitMaxDelay(60).build(), 1, 2, 4, 8, 16, 32, 60);
        assertAllDelays(longs().limitMaxDelay(60).limitMaxAttempts(7).build(), 1, 2, 4, 8, 16, 32, 60);
        assertAllDelays(longs().limitMaxDelay(100).limitMaxAttempts(7).build(), 1, 2, 4, 8, 16, 32, 64);
        assertAllDelays(longs().limitMaxAttempts(7).limitMaxDelay(100).build(), 1, 2, 4, 8, 16, 32, 64);
    }
    
    @Test
    public void limitMaxIncrementsAndMaxDelay() {
        assertFirstDelays(longs().limitMaxIncrements(7).limitMaxDelay(4).build(), 1, 2, 4, 4, 4, 4, 4);
        assertFirstDelays(longs().limitMaxIncrements(7).limitMaxDelay(60).build(), 1, 2, 4, 8, 16, 32, 60);
        assertFirstDelays(longs().limitMaxDelay(60).limitMaxIncrements(7).build(), 1, 2, 4, 8, 16, 32, 60);
        assertFirstDelays(longs().limitMaxDelay(100).limitMaxIncrements(7).build(), 1, 2, 4, 8, 16, 32, 64, 100, 100);
        assertFirstDelays(longs().limitMaxIncrements(7).limitMaxDelay(100).build(), 1, 2, 4, 8, 16, 32, 64, 100, 100);
        ExponentialDelays.OfLong original = longsFrom(5).limitMaxIncrements(4);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxDelay(80));
        original = longsFrom(3).limitMaxDelay(95);
        assertSame("Missing optimization for unnecessary operation", original, original.limitMaxIncrements(5));
    }
    
    private static void assertFirstDelays(LongStream stream, long... delays) {
        assertNextDelays(stream.iterator(), delays);
    }
    
    private static void assertAllDelays(LongStream stream, long... delays) {
        PrimitiveIterator.OfLong iterator = stream.iterator();
        assertNextDelays(iterator, delays);
        if (iterator.hasNext()) {
            fail("More delays follow after " + print(delays) + ": " + iterator.nextLong() + (iterator.hasNext() ? "..." : ""));
        }
    }
    
    private static void assertNextDelays(PrimitiveIterator.OfLong iterator, long... delays) {
        int expectedCount = delays.length;
        for (int i = 0; i < expectedCount; ++i) {
            long expected = delays[i];
            String pre = print(Arrays.copyOf(delays, i));
            if (iterator.hasNext()) {
                assertEquals("After: " + pre, expected, iterator.nextLong());
            } else {
                fail("Expected: " + print(delays) + " but was: " + pre);
            }
        }
    }
    
    private static String print(long... array) {
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
