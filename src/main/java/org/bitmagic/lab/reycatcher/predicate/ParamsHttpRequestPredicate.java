package org.bitmagic.lab.reycatcher.predicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author yangrd
 */
public class ParamsHttpRequestPredicate implements HttpRequestPredicate0 {
    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
        return kvs.entrySet().stream().anyMatch(e->{
            String paramVal = request.getParameter(e.getKey());
            return Objects.nonNull(paramVal) && ("true".equals(e.getValue())||Pattern.matches(e.getValue(), paramVal));
        });
    }
}
