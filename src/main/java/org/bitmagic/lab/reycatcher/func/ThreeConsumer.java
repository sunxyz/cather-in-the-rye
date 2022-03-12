package org.bitmagic.lab.reycatcher.func;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author yangrd
 */
@FunctionalInterface
public interface ThreeConsumer<T, U, V> {

    void accept(T v1, U v2, V v3);

    static <T, U, V> ThreeConsumer<T, U, V> of(BiConsumer<? super T, ? super U> consumer) {
        Objects.requireNonNull(consumer);
        return (v1, v2, v3) -> {
            consumer.accept(v1, v2);
        };
    }

    static <T, U, V> ThreeConsumer<T, U, V> of(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return (v1, v2, v3) -> {
            consumer.accept(v1);
        };
    }

    static <T, U, V> ThreeConsumer<T, U, V> of(NoArgsHandler handler) {
        Objects.requireNonNull(handler);
        return (v1, v2, v3) -> {
            handler.handler();
        };
    }


    default ThreeConsumer<T, U, V> andThen(ThreeConsumer<? super T, ? super U, ? super V> consumer) {
        Objects.requireNonNull(consumer);
        return (v1, v2, v3) -> {
            this.accept(v1, v2, v3);
            consumer.accept(v1, v2, v3);
        };
    }
}
