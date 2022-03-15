package org.bitmagic.lab.reycatcher.urimatches;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.func.NoArgsHandler;
import org.bitmagic.lab.reycatcher.func.ThreeConsumer;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author yangrd
 */
public interface UriMatchesHandlerBuilder {

    AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    static UriMatchesHandlerBuilder of(Predicate<HttpServletRequest> predicate) {
        return UriMatchesHandlerBuilder.SimpleUriMatchHandlerBuilder.of(new ArrayList<>(Collections.singletonList(predicate)));
    }

    UriMatchesHandlerBuilder matches(String... uris);

    UriMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate);

    UriMatchesHandlerBuilder notMatches(Predicate<HttpServletRequest> predicate);

    UriMatchesHandlerBuilder notMatches(String... uris);

    UriMatchesHandlerBuilder childScope(UriMatchesHandler... urlMatches);

    UriMatchesHandler setHandler(NoArgsHandler handler);

    UriMatchesHandler setHandler(Consumer<HttpServletRequest> handler);

    UriMatchesHandler setHandler(BiConsumer<HttpServletRequest, HttpServletResponse> handler);

    UriMatchesHandler setHandler(ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> handler);

    UriMatchesHandler build();

    @RequiredArgsConstructor(staticName = "of")
    class SimpleUriMatchHandlerBuilder implements UriMatchesHandlerBuilder {
        static final ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> NO_ARGS_HANDLER = ThreeConsumer.of(() -> {
        });
        final List<Predicate<HttpServletRequest>> matchPredicates;
        List<Predicate<HttpServletRequest>> notMatchPredicates = new ArrayList<>();
        List<UriMatchesHandler> children = new ArrayList<>();

        @Override
        public UriMatchesHandlerBuilder matches(String... uris) {
            matchPredicates.add(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
            return this;
        }

        @Override
        public UriMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate) {
            matchPredicates.add(predicate);
            return this;
        }

        @Override
        public UriMatchesHandlerBuilder notMatches(Predicate<HttpServletRequest> predicate) {
            notMatchPredicates.add(predicate);
            return this;
        }

        @Override
        public UriMatchesHandlerBuilder notMatches(String... uris) {
            notMatchPredicates.add(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
            return this;
        }

        @Override
        public UriMatchesHandlerBuilder childScope(UriMatchesHandler... urlMatches) {
            children.addAll(Arrays.asList(urlMatches));
            return this;
        }

        @Override
        public UriMatchesHandler setHandler(NoArgsHandler handler) {
            return setHandler(ThreeConsumer.of(handler));
        }

        @Override
        public UriMatchesHandler setHandler(Consumer<HttpServletRequest> handler) {
            return setHandler(ThreeConsumer.of(handler));
        }

        @Override
        public UriMatchesHandler setHandler(BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
            return setHandler(ThreeConsumer.of(handler));
        }

        @Override
        public UriMatchesHandler setHandler(ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> handler) {
            return UriMatchesHandler.SimpleUriMatchesHandler.of(matchPredicates, notMatchPredicates, (x, y, z) -> {
                handler.accept(x, y, z);
                String res = z.getReturnRes();
                z.restReturnRes();
                return Objects.isNull(res);
            }, children);
        }

        @Override
        public UriMatchesHandler build() {
            return setHandler(NO_ARGS_HANDLER);
        }
    }
}
