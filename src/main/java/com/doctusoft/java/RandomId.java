package com.doctusoft.java;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Provides random String ID generation suitable for distributed systems.
 * <p>
 * The generated random ID is a Base64 URL-safe encoded 128bit number. The first 64bit is derived from the number of 
 * nanoseconds since epoch(0) (in 100ns precision), while the second 64 bit is a random generated long value.
 * </p><p>
 * The generated String is always 22 characters long containing only [0-9A-Za-z] '-' and '_' characters.
 * </p><p>
 * The time component (first 64bit) guaranties causal ordering for the generated IDs making them practical to be used
 * as identifiers / primary keys in databases or distributed systems.
 * </p>
 */
public final class RandomId {
    
    private RandomId() { throw Failsafe.staticClassInstantiated(); }
    
    /**
     * @return a 22-char long URL-safe random identifier
     */
    public static String generate() {
        Instant now = Instant.now(CLOCK);
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.asLongBuffer()
            .put(now.getEpochSecond() * 10_000_000L + (now.getNano() / 100))
            .put(ThreadLocalRandom.current().nextLong());
        return CODER.encodeToString(buf.array());
    }
    
    private static final PrecisionClock CLOCK = new PrecisionClock();
    
    private static final Base64.Encoder CODER = Base64.getUrlEncoder().withoutPadding();
    
}
