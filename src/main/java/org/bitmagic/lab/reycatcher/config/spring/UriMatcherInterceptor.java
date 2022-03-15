package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.urimatches.UriMatchesCreate;
import org.bitmagic.lab.reycatcher.urimatches.UriMatchesHandler;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class UriMatcherInterceptor implements HandlerInterceptor {

    private final UriMatchesHandler uriMatchesHandler;

    public UriMatcherInterceptor(Function<UriMatchesCreate,UriMatchesHandler> uriMatchesHandlerMapper) {
        this.uriMatchesHandler = uriMatchesHandlerMapper.apply(new UriMatchesCreate());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return uriMatchesHandler.handler(request, response);
    }

}
