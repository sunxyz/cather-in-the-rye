package org.bitmagic.lab.reycatcher.reqmatches;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.func.NoArgsHandler;
import org.bitmagic.lab.reycatcher.func.PathMatcher;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author yangrd
 */
public interface ReqMatchesHandlerBuilder {

    PathMatcher ANT_PATH_MATCHER = InstanceHolder.getInstance("antPathMatcher", PathMatcher.class);

    static ReqMatchesHandlerBuilder of(Predicate<HttpServletRequest> predicate) {
        return SimpleReqMatchHandlerBuilder.of(new ArrayList<>(Collections.singletonList(predicate)));
    }

    ReqMatchesHandlerBuilder matches(String... uris);

    ReqMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate);

    ReqMatchesHandlerBuilder notMatches(Predicate<HttpServletRequest> predicate);

    ReqMatchesHandlerBuilder notMatches(String... uris);

    ReqMatchesHandlerBuilder childScope(ReqMatchesHandler... urlMatches);

    ReqMatchesHandler setHandler(NoArgsHandler handler);

    ReqMatchesHandler setHandler(Consumer<HttpServletRequest> handler);

    ReqMatchesHandler setHandler(BiConsumer<HttpServletRequest, ReqMatchesFunc> handler);

    ReqMatchesHandler build();

    @RequiredArgsConstructor(staticName = "of")
    class SimpleReqMatchHandlerBuilder implements ReqMatchesHandlerBuilder {
        static final BiConsumer<HttpServletRequest, ReqMatchesFunc> NO_ARGS_CONSUMER = BiConsumers.of(() -> {
        });
        final List<Predicate<HttpServletRequest>> matchPredicates;
        List<Predicate<HttpServletRequest>> notMatchPredicates = new ArrayList<>();
        List<ReqMatchesHandler> children = new ArrayList<>();

        @Override
        public ReqMatchesHandlerBuilder matches(String... uris) {
            matchPredicates.add(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
            return this;
        }

        @Override
        public ReqMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate) {
            matchPredicates.add(predicate);
            return this;
        }

        @Override
        public ReqMatchesHandlerBuilder notMatches(Predicate<HttpServletRequest> predicate) {
            notMatchPredicates.add(predicate);
            return this;
        }

        @Override
        public ReqMatchesHandlerBuilder notMatches(String... uris) {
            notMatchPredicates.add(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
            return this;
        }

        @Override
        public ReqMatchesHandlerBuilder childScope(ReqMatchesHandler... urlMatches) {
            children.addAll(Arrays.asList(urlMatches));
            return this;
        }

        @Override
        public ReqMatchesHandler setHandler(NoArgsHandler handler) {
            return setHandler(BiConsumers.of(handler));
        }

        @Override
        public ReqMatchesHandler setHandler(Consumer<HttpServletRequest> handler) {
            return setHandler(BiConsumers.of(handler));
        }

        @Override
        public ReqMatchesHandler setHandler(BiConsumer<HttpServletRequest, ReqMatchesFunc> handler) {
            return ReqMatchesHandler.SimpleReqMatchesHandler.of(matchPredicates, notMatchPredicates, (x, y) -> {
                handler.accept(x, y);
                return !RcRequestContextHolder.getContext().containsAttribute(ReqMatchesHandler.RES_FLAG);
            }, children);
        }

        @Override
        public ReqMatchesHandler build() {
            return setHandler(NO_ARGS_CONSUMER);
        }
    }
}
