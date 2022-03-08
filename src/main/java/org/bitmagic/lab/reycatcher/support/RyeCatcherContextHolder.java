package org.bitmagic.lab.reycatcher.support;

/**
 * @author yangrd
 * @date 2022/03/08
 */
public class RyeCatcherContextHolder {

    private static final RyeCatcherContext INSTANCE = new RyeCatcherContext() {
    };

    public static RyeCatcherContext getContext(){
        return INSTANCE;
    }
}
