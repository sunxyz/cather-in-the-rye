package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.UriMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public  class UriInterceptor implements HandlerInterceptor, UriMatcher<UriInterceptor> {

    private final Map<String,BiConsumer<HttpServletRequest, HttpServletResponse>> uri2Handler = new HashMap<>();

    private String tempUri = null;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        uri2Handler.forEach((uriKey, handler0)->{
            if(ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())){
                handler0.accept(request,response);
            }
        });
        return true;
    }

    @Override
    public UriInterceptor matchHandler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        uri2Handler.put(matchPath, handler);
        return this;
    }

    @Override
    public UriInterceptor match(String matchPath) {
        tempUri = matchPath;
        return this;
    }

    @Override
    public UriInterceptor handler(BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        uri2Handler.put(tempUri, handler);
        tempUri = null;
        return this;
    }
}
