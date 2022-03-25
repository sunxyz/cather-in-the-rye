package org.bitmagic.lab.reycatcher.predicates;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangrd
 */
public class CompositeCertificationSystemPredicate implements CertificationSystemPredicate {
    private final Map<String,CertificationSystemPredicate> strategies;

    public CompositeCertificationSystemPredicate(List<CertificationSystemPredicate> certificationSystemPredicates) {
        this.strategies = certificationSystemPredicates.stream().collect(Collectors.toMap(o->o.getClass().getSimpleName().replace(CertificationSystemPredicate.class.getSimpleName(),""), Function.identity()));
    }

    @Override
    public boolean test(String name, HttpServletRequest request, Map<String, String> kvs) {
        return strategies.get(name).test(name,request,kvs);
    }
}
