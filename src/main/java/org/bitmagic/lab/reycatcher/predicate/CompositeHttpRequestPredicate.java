package org.bitmagic.lab.reycatcher.predicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangrd
 */
public class CompositeHttpRequestPredicate implements HttpRequestPredicate {
    private final Map<String, HttpRequestPredicate> strategies;

    public CompositeHttpRequestPredicate(Collection<HttpRequestPredicate> httpRequestPredicates) {
        HttpRequestPredicate systemPredicate = httpRequestPredicates.iterator().next();
        if (systemPredicate instanceof CompositeHttpRequestPredicate) {
            this.strategies = ((CompositeHttpRequestPredicate) systemPredicate).strategies;
        } else {
            this.strategies = httpRequestPredicates.stream().collect(Collectors.toMap(o -> o.getClass().getSimpleName().replace(HttpRequestPredicate.class.getSimpleName(), ""), Function.identity()));
        }
    }

    @Override
    public boolean test(String name, HttpServletRequest request, Map<String, String> kvs) {
        return strategies.get(name).test(name, request, kvs);
    }
}
