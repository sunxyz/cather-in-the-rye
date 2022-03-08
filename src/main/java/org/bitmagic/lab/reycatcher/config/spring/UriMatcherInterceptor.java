package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.UriMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class UriMatcherInterceptor implements HandlerInterceptor, UriMatcher<UriMatcherInterceptor> {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static final ThreadLocal<Boolean> STOP_NEXT_CACHE = ThreadLocal.withInitial(() -> false);
    private final Map<String, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatcherInterceptor>> uri2Handler = new HashMap<>();
    private final List<String> matchUriKeys = new ArrayList<>();
    private final Set<String> notMatchUriKeys = new HashSet<>();
    private final List<String> tempUriKeys = new ArrayList<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean matchFlag = notMatchUriKeys.stream().noneMatch(uriKey->ANT_PATH_MATCHER.match(uriKey, request.getRequestURI()));
        if(matchFlag){
            matchUriKeys.forEach(uriKey -> {
                if (ANT_PATH_MATCHER.match(uriKey, request.getRequestURI()) && !isStopNext()) {
                    uri2Handler.get(uriKey).accept(request, response, this);
                }
            });
            clear();
        }
        return true;
    }

    @Override
    public UriMatcherInterceptor matchHandler(String matchPath, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatcherInterceptor> handler) {
        uri2Handler.put(matchPath, handler);
        matchUriKeys.add(matchPath);
        return this;
    }

    @Override
    public UriMatcherInterceptor matchHandler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        uri2Handler.put(matchPath, ThreeConsumer.of(handler));
        matchUriKeys.add(matchPath);
        return this;
    }

    @Override
    public UriMatcherInterceptor noMatch(String... matchPath) {
        notMatchUriKeys.addAll(Arrays.asList(matchPath));
        return this;
    }

    @Override
    public UriMatcherInterceptor match(String... matchPath) {
        tempUriKeys.addAll(Arrays.asList(matchPath));
        return this;
    }


    @Override
    public UriMatcherInterceptor handler(ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatcherInterceptor> handler) {
        tempUriKeys.forEach(uriKey -> matchHandler(uriKey, handler));
        this.clear();
        return this;
    }

    @Override
    public UriMatcherInterceptor handler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        tempUriKeys.forEach(uriKey -> matchHandler(uriKey, handler));
        this.clear();
        return this;
    }


    @Override
    public void stopNext() {
        STOP_NEXT_CACHE.set(true);
    }

    private boolean isStopNext() {
        return STOP_NEXT_CACHE.get();
    }

    private void clear() {
        STOP_NEXT_CACHE.set(false);
    }
}
