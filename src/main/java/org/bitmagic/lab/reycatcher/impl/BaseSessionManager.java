package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;

import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class BaseSessionManager extends AbstractSessionManager {

    private final TokenGenService tokenGenService;

    public BaseSessionManager(SessionRepository repository, TokenGenService tokenGenService) {
        super(repository);
        this.tokenGenService = tokenGenService;
    }

    @Override
    public Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta) {
        return Session.of(tokenGenService.genToken(id, deviceType, sessionTokenType, clientExtMeta), LoginInfo.of(id, deviceType), meta);
    }

    @Override
    public Optional<Session> getCurrentSession(String tokenName) {
        return findSessionTokenFromClient(tokenName).map(sessionToken -> {
            renewal(sessionToken);
            if(Config.isNeedSave(sessionToken.getType())){
                return findByToken(sessionToken).orElse(null);
            }else if(SessionToken.TokenTypeCons.JWT_TOKEN.equals(sessionToken.getType())){
                LoginInfo loginInfo = null;// jwt->login-info
                Object meta = null; //jwt->login-info
                return  Session.of(sessionToken, loginInfo, meta);
            }else {
                return null;
            }
        });
    }

    @Override
    public Optional<SessionToken> findSessionTokenFromClient(String tokenName) {
       throw new RuntimeException();
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        throw new RuntimeException();
    }



}
