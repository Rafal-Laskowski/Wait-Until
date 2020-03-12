package com.laskowski.wait;

import java.util.function.Supplier;

public interface ExpectedCondition<T> extends Supplier<T> {
}
