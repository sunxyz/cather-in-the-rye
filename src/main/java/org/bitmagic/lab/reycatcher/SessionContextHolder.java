package org.bitmagic.lab.reycatcher;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class SessionContextHolder {

    private static final ThreadLocal<SessionContext> CACHE = new ThreadLocal<>();

    public static void setContext(SessionContext reyCatcherContext){
        CACHE.set(reyCatcherContext);
    }

    public static SessionContext getContext(){
        return CACHE.get();
    }

    public static void init(){
        CACHE.remove();
    }
}
