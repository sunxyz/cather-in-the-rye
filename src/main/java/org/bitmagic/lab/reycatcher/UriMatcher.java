package org.bitmagic.lab.reycatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface UriMatcher<T extends UriMatcher<T>> {

    T matchHandler(String matchPath, ThreeConsumer<HttpServletRequest, HttpServletResponse,T> handler);

    T matchHandler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler);

    T noMatch(String... matchPath);

    T match(String... matchPath);

    T handler(ThreeConsumer<HttpServletRequest, HttpServletResponse, T> handler);

    T handler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler);

    void stopNext();

    @FunctionalInterface
    interface ThreeConsumer<T, U, V> {

        void accept(T v1, U v2, V v3);

        default ThreeConsumer<T, U, V> andThen(ThreeConsumer<? super T, ? super U, ? super V> consumer) {
            Objects.requireNonNull(consumer);
            return (v1, v2, v3) -> {
                this.accept(v1, v2, v3);
                consumer.accept(v1, v2, v3);
            };
        }

        default ThreeConsumer<T, U, V> andThen(BiConsumer<? super T, ? super U> consumer) {
            Objects.requireNonNull(consumer);
            return (v1, v2, v3) -> {
                this.accept(v1, v2, v3);
                consumer.accept(v1, v2);
            };
        }

        static <T,U,V> ThreeConsumer<T, U, V> of(BiConsumer<? super T, ? super U> consumer){
            Objects.requireNonNull(consumer);
            return (v1, v2, v3) -> {
                consumer.accept(v1, v2);
            };
        }
    }


}
