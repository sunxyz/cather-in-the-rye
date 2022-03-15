package org.bitmagic.lab.reycatcher.urimatches;

import org.bitmagic.lab.reycatcher.func.ThreeFunction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yangrd
 */
public interface Handler extends ThreeFunction<HttpServletRequest, HttpServletResponse, UriMatchesFunc, Boolean> {
}
