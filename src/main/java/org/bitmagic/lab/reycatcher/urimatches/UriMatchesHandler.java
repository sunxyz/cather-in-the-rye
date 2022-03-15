package org.bitmagic.lab.reycatcher.urimatches;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.support.RyeCatcherContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author yangrd
 */
public interface UriMatchesHandler extends UriMatchesFunc {

    boolean handler(HttpServletRequest request, HttpServletResponse response);

    void setParent(UriMatchesHandler parent);

    @RequiredArgsConstructor
    class SimpleUriMatchesHandler implements UriMatchesHandler {
        final List<Predicate<HttpServletRequest>> matchPredicates;
        final List<Predicate<HttpServletRequest>> notMatchPredicates;
        final Handler handler;
        final List<UriMatchesHandler> children;
        private final ThreadLocal<Boolean> stopNextCache = ThreadLocal.withInitial(() -> false);
        @Setter
        @Getter
        UriMatchesHandler parent;
        private String res;

        public static SimpleUriMatchesHandler of(List<Predicate<HttpServletRequest>> matchPredicates, List<Predicate<HttpServletRequest>> notMatchPredicates, Handler handler, List<UriMatchesHandler> children) {
            SimpleUriMatchesHandler matchHandler = new SimpleUriMatchesHandler(matchPredicates, notMatchPredicates, handler, children);
            children.forEach(c -> c.setParent(matchHandler));
            return matchHandler;
        }


        @Override
        public boolean handler(HttpServletRequest request, HttpServletResponse response) {
            boolean matchFlag = matchPredicates.stream().anyMatch(predicate -> predicate.test(request)) && notMatchPredicates.stream().noneMatch(predicate -> predicate.test(request));
            boolean flag = true;
            if (matchFlag && !isStopNextFlag()) {
                flag = handler.apply(request, response, this) && children.stream().allMatch(c -> c.handler(request, response));
            }
            clearStopNextFlag();
            return flag;
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

        @lombok.SneakyThrows
        @Override
        public void returnRes(String o) {
            this.res = o;
            RyeCatcherContextHolder.getContext().getResponse().getWriter().write(o);
        }

        @Override
        public String getReturnRes() {
            return res;
        }

        @Override
        public void restReturnRes() {
            res = null;
        }

        private boolean isStopNextFlag() {
            return stopNextCache.get();
        }

        private void clearStopNextFlag() {
            stopNextCache.set(false);
        }

    }
}
