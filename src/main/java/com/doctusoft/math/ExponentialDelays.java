package com.doctusoft.math;

import com.doctusoft.annotation.Beta;
import com.doctusoft.java.Failsafe;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Objects.*;

/**
 * This utility helps create streams of exponentially increasing int or long values complying with various criteria.
 * Assembling all those criteria is done in a builder style defined by {@link OfInt} and {@link OfLong} internal
 * interfaces. Invoking their {@code build()} method returns a new {@link IntStream} or {@link LongStream} instance
 * with the desired attributes.
 */
@Beta
public interface ExponentialDelays {
    
    /**
     * @return A new infinite {@link OfInt} builder starting for the default value 1.
     */
    static OfInt ints() { return new IntDelays(1); }
    
    /**
     * @param initialDelay the initial delay of the delay stream under construction
     * @return A new infinite {@link OfInt} builder starting for the defined initial delay
     */
    static OfInt intsFrom(int initialDelay) { return new IntDelays(initialDelay); }
    
    /**
     * Builder for int delay streams.
     */
    interface OfInt {
        
        /**
         * @param maxAttempts the max number of delays returned by the stream under construction
         * @return a new builder limiting the maximum number of delays returned by the stream under construction
         */
        default OfInt limitMaxAttempts(int maxAttempts) {
            return new IntDelaysWithLimitedAttempts(this, maxAttempts);
        }
        
        /**
         * The delay values returned by the stream under construction will be limited by the provided {@code maxDelay}
         * parameter. Any delay value larger than that will be replaced by this {@code mayDelay} value.
         *
         * @param maxDelay the max delay value limit
         * @return a new builder limiting the largest delay value returned by the stream under construction
         */
        default OfInt limitMaxDelay(int maxDelay) {
            if (maxDelay < Integer.MAX_VALUE) {
                return new IntDelaysWithLimitedMaximum(this, maxDelay);
            }
            return this;
        }
        
        /**
         * @param maxIncrements the maximum number of increased delays allowed to be returned by the stream under
         *                      construction
         * @return a new builder limiting the maximum number of increased delays returned by the stream under
         * construction (compared to its predecessor delay value)
         */
        OfInt limitMaxIncrements(int maxIncrements);
        
        /**
         * @return a new {@link IntStream} instance returning the desired int delay values
         */
        IntStream build();
    
        /**
         * @return a finite list containing all delays up until the point where they start repeating
         */
        default List<Integer> buildConfig() {
            PrimitiveIterator.OfInt it = build().iterator();
            if (!it.hasNext()) return Collections.emptyList();
            ArrayList<Integer> config = new ArrayList<>();
            int last = it.nextInt();
            config.add(last);
            while (it.hasNext()) {
                int next = it.nextInt();
                if (next == last) return config;
                config.add(next);
                last = next;
            }
            return config;
        }
    }
    
    /**
     * @return A new infinite {@link OfLong} builder starting for the default value 1L.
     */
    static OfLong longs() { return new LongDelays(1L); }
    
    /**
     * @param initialDelay the initial delay of the delay stream under construction
     * @return A new infinite {@link OfLong} builder starting for the defined initial delay
     */
    static OfLong longsFrom(long initialDelay) { return new LongDelays(initialDelay); }
    
    /**
     * Builder for long delay streams.
     */
    interface OfLong {
        
        default OfLong limitMaxAttempts(int maxAttempts) {
            return new LongDelaysWithLimitedAttempts(this, maxAttempts);
        }
        
        default OfLong limitMaxDelay(long maxDelay) {
            if (maxDelay < Long.MAX_VALUE) {
                return new LongDelaysWithLimitedMaximum(this, maxDelay);
            }
            return this;
        }
        
        OfLong limitMaxIncrements(int maxIncrements);
        
        /**
         * @return a new {@link IntStream} instance returning the desired int delay values
         */
        LongStream build();
        
        /**
         * @return a finite list containing all delays up until the point where they start repeating
         */
        default List<Long> buildConfig() {
            PrimitiveIterator.OfLong it = build().iterator();
            if (!it.hasNext()) return Collections.emptyList();
            ArrayList<Long> config = new ArrayList<>();
            long last = it.nextLong();
            config.add(last);
            while (it.hasNext()) {
                long next = it.nextLong();
                if (next == last) return config;
                config.add(next);
                last = next;
            }
            return config;
        }
    }
    
    /**
     * The maximum possible number of increments in an int delay stream.
     */
    int MAX_INT_INCREMENTS = 31;
    
    /**
     * The maximum possible number of increments in a long delay stream.
     */
    int MAX_LONG_INCREMENTS = 63;
    
    IntUnaryOperator INT_OPERATOR = x -> capMaxInt(x << 1);
    
    LongUnaryOperator LONG_OPERATOR = x -> capMaxLong(x << 1);
    
    static int capMaxInt(int value) {
        return value < 0 ? Integer.MAX_VALUE : value;
    }
    
    static long capMaxLong(long value) {
        return value < 0L ? Long.MAX_VALUE : value;
    }
    
    class IntDelays implements OfInt {
        
        private final int initialDelay;
        
        private IntDelays(int initialDelay) {
            Failsafe.checkArgument(initialDelay > 0, () -> "initialDelay: " + initialDelay);
            this.initialDelay = initialDelay;
        }
        
        public OfInt limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            if (Integer.highestOneBit(initialDelay) + maxIncrements > MAX_INT_INCREMENTS) {
                // since INT_OPERATOR is protected against overflow we have nothing more to do here
                return this;
            }
            return limitMaxDelay(initialDelay << maxIncrements);
        }
        
        public IntStream build() {
            return IntStream.iterate(initialDelay, INT_OPERATOR);
        }
        
    }
    
    class FixedIntDelays implements OfInt {
        
        private final int[] delays;
        
        private FixedIntDelays(int[] delays) {
            this.delays = requireNonNull(delays);
        }
        
        public OfInt limitMaxAttempts(int maxAttempts) {
            Failsafe.checkArgument(maxAttempts > 0, () -> "maxAttempts: " + maxAttempts);
            if (maxAttempts >= delays.length) {
                return this;
            }
            return new FixedIntDelays(Arrays.copyOf(delays, maxAttempts));
        }
        
        public OfInt limitMaxDelay(int maxDelay) {
            checkMaxDelay(delays[0], maxDelay);
            int count = delays.length;
            int lastDelay = delays[count - 1];
            if (lastDelay <= maxDelay) {
                return this;
            }
            int[] newDelays = Arrays.copyOf(delays, count);
            for (int i = count - 1; i > 0; --i) {
                if (newDelays[i] <= maxDelay) break;
                newDelays[i] = maxDelay;
            }
            return new FixedIntDelays(newDelays);
        }
        
        public OfInt limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            int count = delays.length;
            if (maxIncrements >= count) {
                return this;
            }
            int maxDelay = delays[maxIncrements];
            int[] newDelays = Arrays.copyOf(delays, count);
            for (int i = maxIncrements + 1; i < count; ++i) {
                newDelays[i] = maxDelay;
            }
            return new FixedIntDelays(newDelays);
        }
        
        public IntStream build() {
            return IntStream.of(delays);
        }
    }
    
    class IntDelaysWithLimitedAttempts implements OfInt {
        
        private final OfInt delegate;
        private final int maxAttempts;
        
        private IntDelaysWithLimitedAttempts(OfInt delegate, int maxAttempts) {
            this.delegate = requireNonNull(delegate);
            Failsafe.checkArgument(maxAttempts > 0, () -> "maxAttempts: " + maxAttempts);
            this.maxAttempts = maxAttempts;
        }
        
        public OfInt limitMaxAttempts(int maxAttempts) {
            if (maxAttempts < this.maxAttempts) {
                return new IntDelaysWithLimitedAttempts(delegate, maxAttempts);
            }
            return this;
        }
        
        public OfInt limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            if (maxIncrements > MAX_INT_INCREMENTS) {
                // since INT_OPERATOR is protected against overflow we have nothing more to do here
                return this;
            }
            if (maxIncrements >= maxAttempts - 1) {
                // there are even less attempts altogether, so nothing to limit further
                return this;
            }
            int[] delays = delegate.build().limit(1 + maxIncrements).toArray();
            if (delays.length > maxIncrements) {
                return limitMaxDelay(delays[maxIncrements]);
            }
            return new FixedIntDelays(delays);
        }
        
        public IntStream build() {
            return delegate.build().limit(maxAttempts);
        }
    }
    
    class IntDelaysWithLimitedMaximum implements OfInt {
        
        private final OfInt delegate;
        private final int maxDelay;
        
        private IntDelaysWithLimitedMaximum(OfInt delegate, int maxDelay) {
            this.delegate = requireNonNull(delegate);
            checkMaxDelay(delegate.build().findFirst().getAsInt(), maxDelay);
            this.maxDelay = maxDelay;
        }
        
        public OfInt limitMaxDelay(int maxDelay) {
            if (maxDelay < this.maxDelay) {
                return new IntDelaysWithLimitedMaximum(delegate, maxDelay);
            }
            return this;
        }
        
        public OfInt limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            if (maxIncrements > MAX_INT_INCREMENTS) {
                // since INT_OPERATOR is protected against overflow we have nothing more to do here
                return this;
            }
            int[] delays = delegate.build().limit(1 + maxIncrements).toArray();
            if (delays.length > maxIncrements) {
                return limitMaxDelay(delays[maxIncrements]);
            }
            return new FixedIntDelays(delays);
        }
        
        public IntStream build() {
            return delegate.build().map(delay -> Math.min(delay, maxDelay));
        }
    }
    
    static void checkMaxDelay(int initialDelay, int maxDelay) {
        Failsafe.checkArgument(maxDelay > initialDelay, () -> "initialDelay: " + initialDelay + " maxDelay: " + maxDelay);
    }
    
    class LongDelays implements OfLong {
        
        private final long initialDelay;
        
        private LongDelays(long initialDelay) {
            Failsafe.checkArgument(initialDelay > 0, () -> "initialDelay: " + initialDelay);
            this.initialDelay = initialDelay;
        }
        
        public OfLong limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            if (Long.highestOneBit(initialDelay) + maxIncrements > MAX_LONG_INCREMENTS) {
                // since INT_OPERATOR is protected against overflow we have nothing more to do here
                return this;
            }
            return limitMaxDelay(initialDelay << maxIncrements);
        }
        
        public LongStream build() {
            return LongStream.iterate(initialDelay, LONG_OPERATOR);
        }
        
    }
    
    class FixedLongDelays implements OfLong {
        
        private final long[] delays;
        
        private FixedLongDelays(long[] delays) {
            this.delays = requireNonNull(delays);
        }
        
        public OfLong limitMaxAttempts(int maxAttempts) {
            Failsafe.checkArgument(maxAttempts > 0, () -> "maxAttempts: " + maxAttempts);
            if (maxAttempts >= delays.length) {
                return this;
            }
            return new FixedLongDelays(Arrays.copyOf(delays, maxAttempts));
        }
        
        public OfLong limitMaxDelay(long maxDelay) {
            checkMaxLongDelay(delays[0], maxDelay);
            int count = delays.length;
            long lastDelay = delays[count - 1];
            if (lastDelay <= maxDelay) {
                return this;
            }
            long[] newDelays = Arrays.copyOf(delays, count);
            for (int i = count - 1; i > 0; --i) {
                if (newDelays[i] <= maxDelay) break;
                newDelays[i] = maxDelay;
            }
            return new FixedLongDelays(newDelays);
        }
        
        public OfLong limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            int count = delays.length;
            if (maxIncrements >= count) {
                return this;
            }
            long maxDelay = delays[maxIncrements];
            long[] newDelays = Arrays.copyOf(delays, count);
            for (int i = maxIncrements + 1; i < count; ++i) {
                newDelays[i] = maxDelay;
            }
            return new FixedLongDelays(newDelays);
        }
        
        public LongStream build() {
            return LongStream.of(delays);
        }
    }
    
    class LongDelaysWithLimitedAttempts implements OfLong {
        
        private final OfLong delegate;
        private final int maxAttempts;
        
        private LongDelaysWithLimitedAttempts(OfLong delegate, int maxAttempts) {
            this.delegate = requireNonNull(delegate);
            Failsafe.checkArgument(maxAttempts > 0, () -> "maxAttempts: " + maxAttempts);
            this.maxAttempts = maxAttempts;
        }
        
        public OfLong limitMaxAttempts(int maxAttempts) {
            if (maxAttempts < this.maxAttempts) {
                return new LongDelaysWithLimitedAttempts(delegate, maxAttempts);
            }
            return this;
        }
        
        public OfLong limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            if (maxIncrements > MAX_LONG_INCREMENTS) {
                // since INT_OPERATOR is protected against overflow we have nothing more to do here
                return this;
            }
            if (maxIncrements >= maxAttempts - 1) {
                // there are even less attempts altogether, so nothing to limit further
                return this;
            }
            long[] delays = delegate.build().limit(1 + maxIncrements).toArray();
            if (delays.length > maxIncrements) {
                return limitMaxDelay(delays[maxIncrements]);
            }
            return new FixedLongDelays(delays);
        }
        
        public LongStream build() {
            return delegate.build().limit(maxAttempts);
        }
    }
    
    class LongDelaysWithLimitedMaximum implements OfLong {
        
        private final OfLong delegate;
        private final long maxDelay;
        
        private LongDelaysWithLimitedMaximum(OfLong delegate, long maxDelay) {
            this.delegate = requireNonNull(delegate);
            checkMaxLongDelay(delegate.build().findFirst().getAsLong(), maxDelay);
            this.maxDelay = maxDelay;
        }
        
        public OfLong limitMaxDelay(long maxDelay) {
            if (maxDelay < this.maxDelay) {
                return new LongDelaysWithLimitedMaximum(delegate, maxDelay);
            }
            return this;
        }
        
        public OfLong limitMaxIncrements(int maxIncrements) {
            Failsafe.checkArgument(maxIncrements > 0, () -> "maxIncrements: " + maxIncrements);
            if (maxIncrements > MAX_INT_INCREMENTS) {
                // since INT_OPERATOR is protected against overflow we have nothing more to do here
                return this;
            }
            long[] delays = delegate.build().limit(1 + maxIncrements).toArray();
            if (delays.length > maxIncrements) {
                return limitMaxDelay(delays[maxIncrements]);
            }
            return new FixedLongDelays(delays);
        }
        
        public LongStream build() {
            return delegate.build().map(delay -> Math.min(delay, maxDelay));
        }
    }
    
    static void checkMaxLongDelay(long initialDelay, long maxDelay) {
        Failsafe.checkArgument(maxDelay > initialDelay, () -> "initialDelay: " + initialDelay + " maxDelay: " + maxDelay);
    }
    
}
