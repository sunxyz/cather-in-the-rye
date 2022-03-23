package org.bitmagic.lab.reycatcher;

import org.bitmagic.lab.reycatcher.support.ReqTokenInfo;

import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionManager extends SessionRepository {

    Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta);

    Optional<Session> getCurrentSession(String tokenName);

    Optional<ReqTokenInfo> findReqTokenInfoFromClient(String tokenName);

    void outSession2Client(String tokenName, Session session);
}
