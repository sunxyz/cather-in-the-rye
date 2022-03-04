package org.bitmagic.lab.reycatcher;

import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionManager extends SessionRepository {

    Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta);

    Optional<SessionToken> findSessionTokenFromClient(String tokenName);

    void outSession2Client(String tokenName, Session session);
}
