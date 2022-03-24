package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.Session;

import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionContext {

    Optional<Session> findSession();

    static SessionContext ofNullable(Session session){
        return () -> Optional.ofNullable(session);
    }

    static SessionContext of(Session session){
        return () -> Optional.of(session);
    }
}
