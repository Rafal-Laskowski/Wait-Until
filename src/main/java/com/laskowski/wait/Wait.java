package com.laskowski.wait;

import com.google.common.base.Throwables;
import com.laskowski.wait.exceptions.TimeoutException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wait {
    private final static long DEFAULT_SLEEP_TIMEOUT = 500;
    private static final Duration DEFAULT_WAIT_DURATION = Duration.ofMillis(DEFAULT_SLEEP_TIMEOUT);
    private Duration interval = DEFAULT_WAIT_DURATION;
    private Duration timeout = Duration.ofSeconds(5);
    private List<Class<? extends Throwable>> exceptionsToIgnore = new ArrayList<>();
    private String message;
    private Clock clock = Clock.systemDefaultZone();

    public Wait withTimeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public Wait withMessage(String message) {
        this.message = message;
        return this;
    }

    public Wait ignoring(Class<? extends Throwable>... exceptions) {
        exceptionsToIgnore.addAll(Arrays.asList(exceptions));
        return this;
    }

    public <T> T until(ExpectedCondition<T> isTrue) {
        Instant end = clock.instant().plus(timeout);

        Throwable lastException;
        while (true) {
            try {
                T value = isTrue.get();
                if (value != null && (Boolean.class != value.getClass() || Boolean.TRUE.equals(value))) {
                    return value;
                }

                // Clear the last exception; if another retry or timeout exception would
                // be caused by a false or null value, the last exception is not the
                // cause of the timeout.
                lastException = null;
            } catch (Throwable e) {
                lastException = propagateIfNotIgnored(e);
            }

            // Check the timeout after evaluating the function to ensure conditions
            // with a zero timeout can succeed.
            if (end.isBefore(clock.instant())) {
                String timeoutMessage = String.format(
                        "Expected condition failed: %s (tried for %d second(s) with %d milliseconds interval)",
                        message == null ? "waiting for " + isTrue : message,
                        timeout.getSeconds(), interval.toMillis());
                throw timeoutException(timeoutMessage, lastException);
            }

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Throwable propagateIfNotIgnored(Throwable e) {
        for (Class<? extends Throwable> ignoredException : exceptionsToIgnore) {
            if (ignoredException.isInstance(e)) {
                return e;
            }
        }
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
    }

    private RuntimeException timeoutException(String message, Throwable lastException) {
        throw new TimeoutException(message, lastException);
    }
}
