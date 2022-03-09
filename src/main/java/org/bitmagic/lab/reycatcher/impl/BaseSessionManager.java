package org.bitmagic.lab.reycatcher.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.support.AuthorizationInfo;
import org.bitmagic.lab.reycatcher.utils.JwtUtils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class BaseSessionManager extends AbstractSessionManager {

    private final TokenGenFactory tokenGenService;

    public BaseSessionManager(SessionRepository repository, TokenGenFactory tokenGenService) {
        super(repository);
        this.tokenGenService = tokenGenService;
    }

    @Override
    public Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta) {
        return Session.of(tokenGenService.genToken(id, deviceType, sessionTokenType, clientExtMeta), LoginInfo.of(id, deviceType), meta);
    }

    @Override
    public Optional<Session> getCurrentSession(String tokenName) {
        Optional<Session> currentSession = findSessionTokenFromClient(tokenName).map(sessionToken -> {
            renewal(sessionToken);
            if (ConfigHolder.isNeedSave()) {
                return findByToken(sessionToken).orElse(null);
            } else if (SessionToken.TokenTypeCons.JWT.equals(sessionToken.getType())) {
                AuthorizationInfo authorizationInfo = sessionToken.getAuthorizationInfo();
                ValidateUtils.checkBearer(authorizationInfo.getType(), "");
                DecodedJWT jwt = JwtUtils.verifierGetJwt(ConfigHolder.getAlgorithm(), authorizationInfo.getValue());
                LoginInfo loginInfo = LoginInfo.of(jwt.getSubject(), jwt.getClaim("deviceType").asString()); // jwt->login-info
                Object meta = Collections.unmodifiableMap((Map) jwt.getClaim("ext")); //jwt->ext
                return Session.of(sessionToken, loginInfo, meta);
            } else {
                return null;
            }
        });
        return currentSession.map(this::getSwitchSession);
    }

    private Session getSwitchSession(Session session) {
        return findSwitchIdTo(session.getLoginInfo()).map(switchInfo->{
            if (ConfigHolder.isNeedSave()) {
                return findOne(switchInfo.getUserId(), switchInfo.getDeviceType()).orElseGet(()->{
                    Session switchSession = Session.of(SessionToken.of("random", UUID.randomUUID().toString()), switchInfo, new HashMap<>(8));
                    save(switchSession);
                    return switchSession;
                });
            }else {
                return genSession(switchInfo.getUserId(), switchInfo.getDeviceType(), session.getSessionToken().getType(), new HashMap<>(8), null);
            }
        }).orElse(session);
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
