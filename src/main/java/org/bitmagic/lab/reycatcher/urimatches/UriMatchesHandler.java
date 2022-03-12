package org.bitmagic.lab.reycatcher.urimatches;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.func.ThreeConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author yangrd
 */
public interface UriMatchesHandler {

    void handler(HttpServletRequest request, HttpServletResponse response, UriMatchesHandler handler);

    void stopNextMatch();

    void stopAllMatch();

    void setParent(UriMatchesHandler parent);


    @RequiredArgsConstructor
    class SimpleUriMatchesHandler implements UriMatchesHandler {
        final List<Predicate<HttpServletRequest>> matchPredicates;
        final List<Predicate<HttpServletRequest>> notMatchPredicates;
        final ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesHandler> handler;
        final List<UriMatchesHandler> children;
        private final ThreadLocal<Boolean> stopNextCache = ThreadLocal.withInitial(() -> false);
        @Setter
        UriMatchesHandler parent;

        public static SimpleUriMatchesHandler of(List<Predicate<HttpServletRequest>> matchPredicates, List<Predicate<HttpServletRequest>> notMatchPredicates, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesHandler> handler, List<UriMatchesHandler> children) {
            SimpleUriMatchesHandler matchHandler = new SimpleUriMatchesHandler(matchPredicates, notMatchPredicates, handler, children);
            children.forEach(c -> c.setParent(matchHandler));
            return matchHandler;
        }

        @Override
        public void handler(HttpServletRequest request, HttpServletResponse response, UriMatchesHandler handler) {
            boolean matchFlag = matchPredicates.stream().anyMatch(predicate -> predicate.test(request));
            boolean notMatchFlag = notMatchPredicates.stream().anyMatch(predicate -> predicate.test(request));
            if (matchFlag && !notMatchFlag && !isStopNext()) {
                handler.handler(request, response, handler);
                children.forEach(uriMatchHandler -> uriMatchHandler.handler(request, response, uriMatchHandler));
            }
            clear();
        }

        @Override
        public void stopNextMatch() {
            if (parent != null) {
                stopNextCache.set(true);
            }
        }

        @Override
        public void stopAllMatch() {
            stopNextMatch();
            if (parent != null) {
                parent.stopAllMatch();
            }
        }


        private boolean isStopNext() {
            return stopNextCache.get();
        }

        private void clear() {
            stopNextCache.set(false);
        }

    }
}
