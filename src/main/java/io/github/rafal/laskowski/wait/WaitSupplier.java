package io.github.rafal.laskowski.wait;

import java.util.function.Supplier;

public class WaitSupplier<T> extends Wait<Object> {

    public WaitSupplier() {
        super(null);
    }

    public T until(Supplier<T> isTrue) {
        return super.until(object -> isTrue.get());
    }
}
