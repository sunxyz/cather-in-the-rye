package org.bitmagic.lab.reycatcher.urimatches;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author yangrd
 */
public class UriMatchesHandlerBuilderFactory {

    static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    static UriMatchesHandlerBuilder matches(String... uris) {
        return matches(request -> Stream.of(uris).anyMatch(uriKey -> ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
    }

    static UriMatchesHandlerBuilder matches(HttpMethod method, String... uris) {
        return matches(request -> Stream.of(uris).anyMatch(uriKey ->method.matches(request.getMethod())&& ANT_PATH_MATCHER.match(uriKey, request.getRequestURI())));
    }

    static UriMatchesHandlerBuilder matches(Predicate<HttpServletRequest> predicate) {
        return UriMatchesHandlerBuilder.of(predicate);
    }
}
