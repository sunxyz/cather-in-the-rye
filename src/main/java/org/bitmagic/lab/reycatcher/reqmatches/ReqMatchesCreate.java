package org.bitmagic.lab.reycatcher.reqmatches;

import org.bitmagic.lab.reycatcher.func.NoArgsHandler;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author yangrd
 */
public class ReqMatchesCreate {

    static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public ReqMatchesHandlerBuilder matches(String... uris) {
        return matches(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
    }

    public ReqMatchesHandlerBuilder matches(HttpMethod method, String... uris) {
        return matches(request -> Stream.of(uris).anyMatch(uriKey -> method.matches(request.getMethod()) && ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
    }

    public ReqMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate) {
        return ReqMatchesHandlerBuilder.of(predicate);
    }

    //     简写方式
    public ReqMatchesHandler matches(String uri, BiConsumer<HttpServletRequest, ReqMatchesFunc> check) {
        return matches(uri).setHandler(check);
    }

    public ReqMatchesHandler matches(String uri, Consumer<HttpServletRequest> check) {
        return matches(uri).setHandler(check);
    }

    public ReqMatchesHandler matches(String uri, NoArgsHandler check) {
        return matches(uri).setHandler(check);
    }

    public ReqMatchesHandler matches(HttpMethod method, String uri, BiConsumer<HttpServletRequest, ReqMatchesFunc> check) {
        return matches(method, uri).setHandler(check);
    }

    public ReqMatchesHandler matches(HttpMethod method, String uri, Consumer<HttpServletRequest> check) {
        return matches(method, uri).setHandler(check);
    }

    public ReqMatchesHandler matches(HttpMethod method, String uri, NoArgsHandler check) {
        return matches(method, uri).setHandler(check);
    }

    public ReqMatchesHandler matches(Predicate<HttpServletRequest> predicate, BiConsumer<HttpServletRequest, ReqMatchesFunc> check) {
        return matches(predicate).setHandler(check);
    }

    public ReqMatchesHandler matches(Predicate<HttpServletRequest> predicate, Consumer<HttpServletRequest> check) {
        return matches(predicate).setHandler(check);
    }

    public ReqMatchesHandler matches(Predicate<HttpServletRequest> predicate, NoArgsHandler check) {
        return matches(predicate).setHandler(check);
    }

    public ReqMatchesHandler matches(String uri, BiConsumer<HttpServletRequest, ReqMatchesFunc> check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(String uri, Consumer<HttpServletRequest> check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(String uri, NoArgsHandler check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(HttpMethod method, String uri, BiConsumer<HttpServletRequest, ReqMatchesFunc> check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(HttpMethod method, String uri, Consumer<HttpServletRequest> check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(HttpMethod method, String uri, NoArgsHandler check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(Predicate<HttpServletRequest> predicate, BiConsumer<HttpServletRequest, ReqMatchesFunc> check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(Predicate<HttpServletRequest> predicate, Consumer<HttpServletRequest> check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

    public ReqMatchesHandler matches(Predicate<HttpServletRequest> predicate, NoArgsHandler check, ReqMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

}
