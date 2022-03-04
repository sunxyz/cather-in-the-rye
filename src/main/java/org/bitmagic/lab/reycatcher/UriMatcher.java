package org.bitmagic.lab.reycatcher;

import java.util.function.Function;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface UriMatcher {

    UriMatcher matchHandler(String matchPath, Function<UriMatcher,Object> handler);

    UriMatcher match(String matchPath);

    UriMatcher handler(Function<UriMatcher,Object> handler);
}
