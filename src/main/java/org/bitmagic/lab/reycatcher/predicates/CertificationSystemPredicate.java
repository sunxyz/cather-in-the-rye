package org.bitmagic.lab.reycatcher.predicates;

import org.bitmagic.lab.reycatcher.func.ThreePredicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yangrd
 */
public interface CertificationSystemPredicate extends ThreePredicate<String, HttpServletRequest, Map<String, String>> {
}
