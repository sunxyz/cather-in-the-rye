package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.urimatches.UriMatchesHandler;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public abstract class UriMatcherInterceptor implements HandlerInterceptor, Supplier<UriMatchesHandler> {

    private final UriMatchesHandler uriMatchesHandler;

    public UriMatcherInterceptor() {
        this.uriMatchesHandler = get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        uriMatchesHandler.handler(request, response, uriMatchesHandler);
        return true;
    }

}
