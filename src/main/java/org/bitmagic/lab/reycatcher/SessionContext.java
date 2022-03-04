package org.bitmagic.lab.reycatcher;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionContext {

    Session getSession();

    static SessionContext of(Session session){
        return () -> session;
    }
}
