package com.doctusoft.java;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Currently JDK8 internal {@link Clock} implementation returns current instant only by millisecond precision. This
 * wrapper {@link Clock} implementation however improves the precision of the delegate {@link Clock} using 
 * {@link System#nanoTime()} to adjust current instants to the nanosecond precision.
 */
public class PrecisionClock extends Clock {
    
    private final Clock clock;
    
    private final Instant initial;
    
    private final long nanos;
    
    /**
     * Create a new {@link PrecisionClock} using the {@link Clock#systemUTC()} clock as delegate.
     */
    public PrecisionClock() {
        this(Clock.systemUTC());
    }
    
    /**
     * Creates a new clock instance using a {@code delegate}.
     */
    public PrecisionClock(Clock delegate) {
        this.clock = delegate;
        this.initial = delegate.instant();
        this.nanos = getSystemNanos();
    }
    
    public ZoneId getZone() {
        return clock.getZone();
    }
    
    public Instant instant() {
        return initial.plusNanos(getSystemNanos() - nanos);
    }
    
    public Clock withZone(ZoneId zone) {
        return new PrecisionClock(clock.withZone(zone));
    }
    
    protected long getSystemNanos() {
        return System.nanoTime();
    }
    
}
