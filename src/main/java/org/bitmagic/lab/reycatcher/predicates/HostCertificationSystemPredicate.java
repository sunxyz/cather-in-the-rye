package org.bitmagic.lab.reycatcher.predicates;

import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yangrd
 */
public class HostCertificationSystemPredicate implements CertificationSystemPredicate0{

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher(".");
    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
        return kvs.keySet().stream().anyMatch(host->ANT_PATH_MATCHER.match(host,request.getRemoteHost()));
    }
}
