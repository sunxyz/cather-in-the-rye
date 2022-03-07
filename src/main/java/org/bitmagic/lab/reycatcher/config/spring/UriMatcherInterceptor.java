package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.UriMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public  class UriMatcherInterceptor implements HandlerInterceptor, UriMatcher<UriMatcherInterceptor> {

    private final Map<String,BiConsumer<HttpServletRequest, HttpServletResponse>> uri2Handler = new HashMap<>();

    private final List<String> uriKeys = new ArrayList<>();

    private String tempUri = null;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final ThreadLocal<Boolean> STOP_NEXT_CACHE = ThreadLocal.withInitial(()->false);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        uriKeys.forEach(uriKey->{
            if(ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())&&!isStopNext()){
                uri2Handler.get(uriKey).accept(request,response);
            }
        });
        clear();
        return true;
    }

    @Override
    public UriMatcherInterceptor matchHandler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        uri2Handler.put(matchPath, handler);
        uriKeys.add(matchPath);
        return this;
    }

    @Override
    public UriMatcherInterceptor match(String matchPath) {
        tempUri = matchPath;
        return this;
    }

    @Override
    public UriMatcherInterceptor handler(BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        uri2Handler.put(tempUri, handler);
        uriKeys.add(tempUri);
        tempUri = null;
        return this;
    }

    @Override
    public void stopNext() {
        STOP_NEXT_CACHE.set(true);
    }

    private boolean isStopNext(){
        return STOP_NEXT_CACHE.get();
    }

    private void clear(){
        STOP_NEXT_CACHE.set(false);
    }
}
