package org.bitmagic.lab.reycatcher.predicates;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author yangrd
 */
public class HeaderCertificationSystemPredicate implements CertificationSystemPredicate0{

    @Override
    public boolean test(HttpServletRequest request, Map<String, String> kvs) {
      return kvs.entrySet().stream().anyMatch(e-> Pattern.matches(e.getValue(),request.getHeader(e.getKey())));
    }
}
