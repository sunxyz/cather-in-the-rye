package org.bitmagic.lab.reycatcher.predicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yangrd
 */
public class MethodHttpRequestPredicate implements HttpRequestPredicate0 {

    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
        return kvs.keySet().stream().anyMatch(method->request.getMethod().equalsIgnoreCase(method));
    }
}
