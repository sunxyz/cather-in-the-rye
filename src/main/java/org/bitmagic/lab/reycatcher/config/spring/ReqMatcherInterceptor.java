package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.reqmatches.ReqMatcher;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesHandler;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class ReqMatcherInterceptor implements HandlerInterceptor {

    private final ReqMatchesHandler reqMatchesHandler;

    public ReqMatcherInterceptor(Function<ReqMatcher, ReqMatchesHandler> reqMatchesHandler) {
        this.reqMatchesHandler = reqMatchesHandler.apply(ReqMatcher.INSTANCE);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return reqMatchesHandler.handler(request);
    }

}
