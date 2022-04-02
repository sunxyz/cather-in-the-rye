package org.bitmagic.lab.reycatcher.predicate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yangrd
 */
public class CookieHttpRequestPredicate implements HttpRequestPredicate0 {

    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
        if (Objects.nonNull(request.getCookies())) {
            Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            return kvs.entrySet().stream().anyMatch(e -> cookies.containsKey(e.getKey()) && ("true".equals(e.getValue()) || Pattern.matches(e.getValue(), cookies.get(e.getKey()))));
        }
        return false;
    }
}
