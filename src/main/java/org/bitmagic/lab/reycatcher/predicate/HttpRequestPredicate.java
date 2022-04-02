package org.bitmagic.lab.reycatcher.predicate;

import org.bitmagic.lab.reycatcher.func.ThreePredicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yangrd
 * # 匹配规则   predicateName=k[:v][,k1[:v1]]
 * # predicateName: Path,Cookie,Params,Header,Host,Method
 */
public interface HttpRequestPredicate extends ThreePredicate<String, HttpServletRequest, Map<String, String>> {
}
