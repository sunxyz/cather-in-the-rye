package org.bitmagic.lab.reycatcher.predicates;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yangrd
 */
public class MethodCertificationSystemPredicate implements CertificationSystemPredicate0{

    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
        return kvs.keySet().stream().anyMatch(method->request.getMethod().equalsIgnoreCase(method));
    }
}
