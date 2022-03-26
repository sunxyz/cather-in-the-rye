package org.bitmagic.lab.reycatcher.support;

/**
 * @author yangrd
 * @date 2022/03/08
 */
public class RcRequestContextHolder {

    private static RcRequestContext INSTANCE = new ThreadLocalRcRequestContext();

    public static RcRequestContext getContext() {
        return INSTANCE;
    }

    public static void setContext(RcRequestContext rcRequestContext) {
        INSTANCE = rcRequestContext;
    }

    public static void clear() {
        INSTANCE.clearAll();
    }
}
