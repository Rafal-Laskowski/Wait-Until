package io.github.rafal.laskowski.wait;

import java.util.function.Function;

public class WaitFunction<T> extends Wait<T> {

    public WaitFunction(T t) {
        super(t);
    }

    public <R> R until(Function<T, R> isTrue) {
        return super.untilIsTrue(isTrue);
    }
}
