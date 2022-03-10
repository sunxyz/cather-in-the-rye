package org.bitmagic.lab.reycatcher;

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
