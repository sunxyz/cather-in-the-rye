package org.bitmagic.lab.reycatcher.reqmatches;

import org.bitmagic.lab.reycatcher.func.NoArgsHandler;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author yangrd
 */
public class BiConsumers {

    public static <T, U> BiConsumer<T, U> of(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return (v1, v2) -> {
            consumer.accept(v1);
        };
    }

    static <T, U> BiConsumer<T, U> of(NoArgsHandler handler) {
        Objects.requireNonNull(handler);
        return (v1, v2) -> {
            handler.handler();
        };
    }
}
