package io.github.rafal.laskowski.wait;

import java.util.function.Function;

public interface ExpectedConditionWithArgument<T, R> extends Function<T, R> {
}
