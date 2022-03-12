package org.bitmagic.lab.reycatcher.urimatches;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.func.NoArgsHandler;
import org.bitmagic.lab.reycatcher.func.ThreeConsumer;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author yangrd
 */
public interface UriMatchesHandlerBuilder {

    static UriMatchesHandlerBuilder of(Predicate<HttpServletRequest> predicate) {
        return UriMatchesHandlerBuilder.SimpleUriMatchHandlerBuilder.of(new ArrayList<>(Collections.singletonList(predicate)));
    }

    UriMatchesHandlerBuilder matches(String... uris);

    UriMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate);

    UriMatchesHandlerBuilder notMatches(Predicate<HttpServletRequest> predicate);

    UriMatchesHandlerBuilder notMatches(String... uris);

    UriMatchesHandlerBuilder childScope(UriMatchesHandler matchUri);

    UriMatchesHandler addHandler(NoArgsHandler handler);

    UriMatchesHandler addHandler(Consumer<HttpServletRequest> handler);

    UriMatchesHandler addHandler(BiConsumer<HttpServletRequest, HttpServletResponse> handler);

    UriMatchesHandler addHandler(ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesHandler> handler);

    AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @RequiredArgsConstructor(staticName = "of")
    class SimpleUriMatchHandlerBuilder implements UriMatchesHandlerBuilder {
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
        public UriMatchesHandlerBuilder childScope(UriMatchesHandler matchUri) {
            children.add(matchUri);
            return this;
        }

        @Override
        public UriMatchesHandler addHandler(NoArgsHandler handler) {
            return addHandler(ThreeConsumer.of(handler));
        }

        @Override
        public UriMatchesHandler addHandler(Consumer<HttpServletRequest> handler) {
            return addHandler(ThreeConsumer.of(handler));
        }

        @Override
        public UriMatchesHandler addHandler(BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
            return addHandler(ThreeConsumer.of(handler));
        }

        @Override
        public UriMatchesHandler addHandler(ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesHandler> handler) {
            return UriMatchesHandler.SimpleUriMatchesHandler.of(matchPredicates, notMatchPredicates, handler, children);
        }
    }
}
