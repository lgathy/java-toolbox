package com.doctusoft.java;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Provides random String ID generation suitable for distributed systems.
 * <p>
 * The generated random ID is a Base64 URL-safe encoded 128bit number. There are 3 different mechanisms for generating
 * the 128bit number each providing different distribution characteristics of the generated IDs suitable for different
 * use-cases.
 * </p><p>
 * The generated String is always 22 characters long containing only [0-9A-Za-z] '-' and '_' characters.
 * </p><p>
 * All random parts of IDs are pseudo random numbers generated with {@link ThreadLocalRandom}. If you need to generate
 * cryptographically secure random values you should not delegate it to a static method invocation anyway. Consider 
 * designing and implementing your own mechanism using {@link java.security.SecureRandom} instead.
 * </p>
 *
 * @see Base64
 * @see PrecisionClock
 */
public final class RandomId {
    
    private RandomId() { throw Failsafe.staticClassInstantiated(); }
    
    /**
     * Generates a new random ID with sequential distribution guaranteed. The first 64bit is derived from the number of
     * nanoseconds since epoch(0) (in 100ns precision), while the second 64 bit is a random generated long value.
     * <p>The time component as the first part guaranties causal ordering for the generated IDs making them practical
     * to be used as primary keys in large SQL database tables, especially if
     * <ul>
     * <li>the record size of the table is relatively small,</li>
     * <li>the IDs are generated in distributed nodes,</li>
     * <li>the storage mechanism prefers sequential IDs upon insertion.</li>
     * </ul></p>
     * <p>This version is also preferable in case of request IDs in distributed systems.</p>
     *
     * @return a 22-char long URL-safe random identifier
     */
    public static String sequential() {
        Instant now = Instant.now(CLOCK);
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.asLongBuffer()
            .put(now.getEpochSecond() * 10_000_000L + (now.getNano() / 100))
            .put(ThreadLocalRandom.current().nextLong())
        ;
        return CODER.encodeToString(buf.array());
    }
    
    /**
     * Similar to {@link #sequential()} but the order of the 64bit segments are reversed: first comes the random 64 bit
     * followed by the time component.
     * <p>This version is preferable for primary keys of large, distributed - usually NoSQL - tables where uniform
     * distribution of IDs are preferred for performance reasons in case of huge throughput bulk insertion workloads.</p>
     *
     * @return a 22-char long URL-safe random identifier
     */
    public static String uniformDistribution() {
        Instant now = Instant.now(CLOCK);
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.asLongBuffer()
            .put(ThreadLocalRandom.current().nextLong())
            .put(now.getEpochSecond() * 10_000_000L + (now.getNano() / 100))
        ;
        return CODER.encodeToString(buf.array());
    }
    
    /**
     * This versions generates 128bit random IDs. It is most preferable in use-cases where there is no point in having
     * a time component encoded into the ID or it is even contraindicated or prohibited.
     * <p>This is in fact quite similar to a type 4 {@link UUID#randomUUID()} except it's more compact (only takes 22
     * characters instead of 36)</p>
     *
     * @return a 22-char long URL-safe random identifier
     */
    public static String fullRandom() {
        Instant now = Instant.now(CLOCK);
        ByteBuffer buf = ByteBuffer.allocate(16);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        buf.asLongBuffer()
            .put(random.nextLong())
            .put(random.nextLong())
        ;
        return CODER.encodeToString(buf.array());
    }
    
    private static final PrecisionClock CLOCK = new PrecisionClock();
    
    private static final Base64.Encoder CODER = Base64.getUrlEncoder().withoutPadding();
    
}
