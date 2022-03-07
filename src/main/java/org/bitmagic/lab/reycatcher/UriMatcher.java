package org.bitmagic.lab.reycatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiConsumer;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface UriMatcher<T extends UriMatcher> {

    T matchHandler(String matchPath, BiConsumer<HttpServletRequest, HttpServletResponse> handler);

    T match(String matchPath);

    T handler(BiConsumer<HttpServletRequest,HttpServletResponse> handler);
}
