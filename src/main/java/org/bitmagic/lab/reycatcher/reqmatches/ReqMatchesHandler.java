package org.bitmagic.lab.reycatcher.reqmatches;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author yangrd
 */
public interface ReqMatchesHandler extends ReqMatchesFunc {

     String  RES_FLAG = "RES_FLAG";

    boolean handler(HttpServletRequest request);

    void setParent(ReqMatchesHandler parent);

    @RequiredArgsConstructor
    class SimpleReqMatchesHandler implements ReqMatchesHandler {
        final List<Predicate<HttpServletRequest>> matchPredicates;
        final List<Predicate<HttpServletRequest>> notMatchPredicates;
        final Handler handler;
        final List<ReqMatchesHandler> children;
        private final ThreadLocal<Boolean> stopNextCache = ThreadLocal.withInitial(() -> false);
        @Setter
        @Getter
        ReqMatchesHandler parent;

        public static SimpleReqMatchesHandler of(List<Predicate<HttpServletRequest>> matchPredicates, List<Predicate<HttpServletRequest>> notMatchPredicates, Handler handler, List<ReqMatchesHandler> children) {
            SimpleReqMatchesHandler matchHandler = new SimpleReqMatchesHandler(matchPredicates, notMatchPredicates, handler, children);
            children.forEach(c -> c.setParent(matchHandler));
            return matchHandler;
        }


        @Override
        public boolean handler(HttpServletRequest request) {
            boolean matchFlag = matchPredicates.stream().anyMatch(predicate -> predicate.test(request)) && notMatchPredicates.stream().noneMatch(predicate -> predicate.test(request));
            boolean flag = true;
            if (matchFlag && !isStopNextFlag()) {
                flag = handler.apply(request, this) && children.stream().allMatch(c -> c.handler(request));
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
            RcRequestContextHolder.getContext().setAttr(RES_FLAG,true);
            RcRequestContextHolder.getContext().getResponse().getWriter().write(o);
        }

        private boolean isStopNextFlag() {
            return stopNextCache.get();
        }

        private void clearStopNextFlag() {
            stopNextCache.set(false);
        }

    }
}
