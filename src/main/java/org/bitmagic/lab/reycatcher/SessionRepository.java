package org.bitmagic.lab.reycatcher;

import java.util.Collection;
import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionRepository extends SessionDurationRenewal ,IdentitySwitch{

    void save(Session session);

    void remove(Session session);

    Optional<Session> findBySessionId(String sessionId);

    /**
     * findLastSessionByLoginInfo
     * @param id
     * @param deviceType
     * @return
     */
    Optional<Session> findByLoginInfo(Object id, String deviceType);

    Optional<Session> findByToken(SessionToken token);

    Page<Session> findAll(SessionFilterInfo filterInfo, int size, int page);

    Collection<Session> listAll(SessionFilterInfo filterInfo);
}
