package org.bitmagic.lab.reycatcher.func;

/**
 * @author yangrd
 */
@FunctionalInterface
public interface PathMatcher {

    /**
     * match
     * @param pattern
     * @param path
     * @return
     */
    boolean match(String pattern, String path);
}
