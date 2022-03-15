package org.bitmagic.lab.reycatcher.urimatches;

import org.bitmagic.lab.reycatcher.func.NoArgsHandler;
import org.bitmagic.lab.reycatcher.func.ThreeConsumer;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author yangrd
 */
public class UriMatchesCreate {

    static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public UriMatchesHandlerBuilder matches(String... uris) {
        return matches(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
    }

    public UriMatchesHandlerBuilder matches(HttpMethod method, String... uris) {
        return matches(request -> Stream.of(uris).anyMatch(uriKey -> method.matches(request.getMethod()) && ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
    }

    public UriMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate) {
        return UriMatchesHandlerBuilder.of(predicate);
    }

//     简写方式

    public UriMatchesHandler matches(String uri, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> check) {
        return matches(uri).setHandler(check);
    }

    public UriMatchesHandler matches(String uri, BiConsumer<HttpServletRequest, HttpServletResponse> check) {
        return matches(uri).setHandler(check);
    }

    public UriMatchesHandler matches(String uri, Consumer<HttpServletRequest> check) {
        return matches(uri).setHandler(check);
    }

    public UriMatchesHandler matches(String uri, NoArgsHandler check) {
        return matches(uri).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> check) {
        return matches(method, uri).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, BiConsumer<HttpServletRequest, HttpServletResponse> check) {
        return matches(method, uri).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, Consumer<HttpServletRequest> check) {
        return matches(method, uri).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, NoArgsHandler check) {
        return matches(method, uri).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> check) {
        return matches(predicate).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, BiConsumer<HttpServletRequest, HttpServletResponse> check) {
        return matches(predicate).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, Consumer<HttpServletRequest> check) {
        return matches(predicate).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, NoArgsHandler check) {
        return matches(predicate).setHandler(check);
    }


    public UriMatchesHandler matches(String uri, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(String uri, BiConsumer<HttpServletRequest, HttpServletResponse> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(String uri, Consumer<HttpServletRequest> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(String uri, NoArgsHandler check, UriMatchesHandler... uriMatchesHandler) {
        return matches(uri).childScope(uriMatchesHandler).setHandler(check);
    }


    public UriMatchesHandler matches(HttpMethod method, String uri, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, BiConsumer<HttpServletRequest, HttpServletResponse> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, Consumer<HttpServletRequest> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(HttpMethod method, String uri, NoArgsHandler check, UriMatchesHandler... uriMatchesHandler) {
        return matches(method, uri).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, ThreeConsumer<HttpServletRequest, HttpServletResponse, UriMatchesFunc> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, BiConsumer<HttpServletRequest, HttpServletResponse> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, Consumer<HttpServletRequest> check, UriMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

    public UriMatchesHandler matches(Predicate<HttpServletRequest> predicate, NoArgsHandler check, UriMatchesHandler... uriMatchesHandler) {
        return matches(predicate).childScope(uriMatchesHandler).setHandler(check);
    }

}
