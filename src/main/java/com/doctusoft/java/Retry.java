package com.doctusoft.java;

import com.doctusoft.annotation.Beta;
import com.doctusoft.math.ExponentialDelays;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import static java.util.Objects.*;

/**
 * A Retry instance wraps a {@link Callable} object and tries executing it with retrying upon failure after the given
 * delays. The number of retry attempts and the delays (in milliseconds) waited between the retry attempts are provided
 * by a {@link LongStream} instance upon instantiation of the {@link Retry} object.
 * <p><b>Important to note</b>, that it is not allowed to invoke a terminal operator on the provided {@link LongStream}
 * instance outside of the {@link Retry} instance, that would result in an exception when {@link #run()} or
 * {@link #call()} methods are invoked. Also, the Retry instance can be executed exclusively once, any further attempts
 * would result in an exception due to the same reason. If you need to re-execute a retry operation a new {@link Retry}
 * instance needs to be instantiated.</p>
 * <p>Execution details such as failures and retry attempts are logged using {@link Logger java.util.logging}.</p>
 */
@Beta
public class Retry<T> implements Callable<T>, Runnable {
    
    private static final Logger log = Logger.getLogger(Retry.class.getName());
    
    /**
     * Executes a {@link Callable} task according to the provided exponential delays (in milliseconds).
     *
     * @param task   the callable task
     * @param delays builder for a delay values stream (in milliseconds)
     * @param <T>    the return value of the callable task
     * @return the value returned by the first successful execution of the callable task
     * @throws RuntimeException upon permanent failure (after retry attempts are exhausted)
     */
    public static <T> T callWithDelays(Callable<T> task, ExponentialDelays.OfLong delays) {
        Retry<T> retry = new Retry<>(task, delays.build());
        return retry.call();
    }
    
    /**
     * Executes a {@link Callable} task according to the default exponential back-off mechanism starting with a default
     * 1s delay after the first attempt fails with the provided maximum number of retries.
     *
     * @param maxRetries the maximum number of retries
     * @param task       the callable task
     * @param <T>        the return value of the callable task
     * @return the value returned by the first successful execution of the callable task
     * @throws RuntimeException upon permanent failure (after retry attempts are exhausted)
     */
    public static <T> T callWithExponentialBackOff(int maxRetries, Callable<T> task) {
        return callWithDelays(task, ExponentialDelays
            .longsFrom(1000L)
            .limitMaxAttempts(maxRetries + 1));
    }
    
    /**
     * Executes a {@link Runnable} task according to the provided exponential delays (in milliseconds).
     *
     * @param runnable the runnable task
     * @param delays   builder for a delay values stream (in milliseconds)
     * @throws RuntimeException upon permanent failure (after retry attempts are exhausted)
     */
    public static void runWithDelays(Runnable runnable, ExponentialDelays.OfLong delays) {
        callWithDelays(Executors.callable(runnable), delays);
    }
    
    /**
     * Executes a {@link Runnable} task according to the default exponential back-off mechanism starting with a default
     * 1s delay after the first attempt fails with the provided maximum number of retries.
     *
     * @param maxRetries the maximum number of retries
     * @param runnable   the runnable task
     * @throws RuntimeException upon permanent failure (after retry attempts are exhausted)
     */
    public static void runWithExponentialBackOff(int maxRetries, Runnable runnable) {
        callWithExponentialBackOff(maxRetries, Executors.callable(runnable));
    }
    
    private final Callable<T> task;
    
    private final Spliterator.OfLong delays;
    
    private int retryCount = 0;
    
    public Retry(Callable<T> task, LongStream delays) {
        this.task = requireNonNull(task, "task");
        this.delays = requireNonNull(delays, "delays").spliterator();
    }
    
    public void run() {
        call();
    }
    
    public T call() throws RuntimeException {
        while (true) {
            try {
                ++retryCount;
                return task.call();
            } catch (CancellationException e) {
                log.log(Level.SEVERE, "Task canceled", e);
                throw e;
            } catch (Exception e) {
                log.log(Level.WARNING, e, () -> retryCount + ". attempt failed");
                if (!delays.tryAdvance((LongConsumer) this::delay)) {
                    log.log(Level.SEVERE, "Failed permanently: no more retries");
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    protected void delay(long timeToWait) {
        log.log(Level.INFO, () -> "Waiting " + printMillis(timeToWait) + " before retrying...");
        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException ie) {
            log.log(Level.FINE, "Interrupted", ie);
        }
    }
    
    protected static String printMillis(long millis) {
        return millis < 1000L ? millis + "ms" : (millis / 1000L) + "s";
    }
    
}
