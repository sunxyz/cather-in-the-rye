package org.bitmagic.lab.reycatcher;

import java.util.Collection;
import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionRepository {

    void save(Session session);

    void remove(Session session);

    Optional<Session> findOne(Object id, String deviceType);

    Optional<Session> findByToken(SessionToken token);

    Page<Session> findAll(Object filterInfo, int size, int page);

    Collection<Session> listAll(Object filterInfo);
}
