package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class SessionContextHolder {

    static SessionManager sessionManager = InstanceHolder.getInstance(SessionManager.class);

    private static final ThreadLocal<SessionContext> CACHE = ThreadLocal.withInitial(()-> SessionContext.ofNullable(sessionManager.getCurrentSession(ConfigHolder.getOutClientTokenName()).orElseThrow(NotFoundSessionException::new)));

    public static void setContext(SessionContext sessionContext){
        CACHE.set(sessionContext);
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