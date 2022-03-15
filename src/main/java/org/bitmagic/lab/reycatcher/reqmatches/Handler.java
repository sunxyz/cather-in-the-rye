package org.bitmagic.lab.reycatcher.reqmatches;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiFunction;

/**
 * @author yangrd
 */
public interface Handler extends BiFunction<HttpServletRequest, ReqMatchesFunc, Boolean> {
}
