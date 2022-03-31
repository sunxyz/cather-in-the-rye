package org.bitmagic.lab.reycatcher.predicates;

import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.func.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangrd
 */
public class PathCertificationSystemPredicate implements CertificationSystemPredicate0{
    static final PathMatcher PATH_MATCHER = InstanceHolder.getInstance("antPathMatcher", PathMatcher.class);
    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
        return kvs.keySet().stream().anyMatch(path-> Objects.nonNull(request.getRequestURI())&&PATH_MATCHER.match(path, request.getRequestURI()));
    }
}
