package org.bitmagic.lab.reycatcher;

import org.bitmagic.lab.reycatcher.config.ConfigHolder;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class SessionContextHolder {

    static SessionManager sessionManager;

    private static final ThreadLocal<SessionContext> CACHE = ThreadLocal.withInitial(()-> SessionContext.ofNullable(sessionManager.getCurrentSession(ConfigHolder.getOutClientTokenName()).orElse(null)));

    public static void setContext(SessionContext reyCatcherContext){
        CACHE.set(reyCatcherContext);
    }

    public static SessionContext getContext(){
        return CACHE.get();
    }

    /**
     * 请求结束后一定要清理
     */
    public static void clear(){
        CACHE.remove();
    }
}
