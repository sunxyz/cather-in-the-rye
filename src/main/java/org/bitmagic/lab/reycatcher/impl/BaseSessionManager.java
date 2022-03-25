package org.bitmagic.lab.reycatcher.impl;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;
import org.bitmagic.lab.reycatcher.ex.ReplacedException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.bitmagic.lab.reycatcher.utils.JwtUtils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class BaseSessionManager extends AbstractSessionManager {

    private final SessionTokenGenFactory tokenGenService;

    public BaseSessionManager(SessionRepository repository, SessionTokenGenFactory tokenGenService) {
        super(repository);
        this.tokenGenService = tokenGenService;
    }

    @Override
    public Session genSession(Object id, String deviceType, String sessionTokenType, Object meta, Object clientExtMeta) {
        return Session.of(tokenGenService.genToken(id, deviceType, sessionTokenType, clientExtMeta), LoginInfo.of(id, deviceType), meta);
    }

    @Override
    public Optional<Session> getCurrentSession(String tokenName) {
        Optional<Session> currentSession = findReqTokenInfoFromClient(tokenName).map(reqTokenInfo -> {
            SessionToken sessionToken = SessionToken.of(DynamicRcConfigHolder.getGenTokenType(), reqTokenInfo.getValue());
            if (DynamicRcConfigHolder.isNeedSave()) {
                Session session = findByToken(sessionToken).orElseThrow(NotFoundSessionException::new);
                if (session.isReplaced()) {
                    remove(session);
                    throw new ReplacedException(String.format("token:%s, loginId:%s,loginType:%s", reqTokenInfo.getValue(), session.getLoginInfo().getUserId(), session.getLoginInfo().getDeviceType()));
                }
                renewal(sessionToken);
                return session;
            } else if (SessionToken.GenTypeCons.JWT.equals(sessionToken.getGenType())) {
                ValidateUtils.checkBearer(reqTokenInfo.getType(), "");
                DecodedJWT jwt = JwtUtils.verifierGetJwt(DynamicRcConfigHolder.getAlgorithm(), reqTokenInfo.getValue());
                LoginInfo loginInfo = LoginInfo.of(jwt.getSubject(), jwt.getClaim("deviceType").asString()); // jwt->login-info
                Claim ext = jwt.getClaim("ext");
                Object meta = Collections.unmodifiableMap(ext.asMap()); //jwt->ext
                return Session.of(sessionToken, loginInfo, meta);
            } else {
                throw new RyeCatcherException("not support");
            }
        });
        return currentSession.map(this::getSwitchSessionOrSelf);
    }

    private Session getSwitchSessionOrSelf(Session session) {
        return findSwitchIdTo(session.getLoginInfo()).map(switchInfo -> {
            if (DynamicRcConfigHolder.isNeedSave()) {
                return findByLoginInfo(switchInfo.getUserId(), switchInfo.getDeviceType()).orElseGet(() -> {
                    Session switchSession = Session.of(SessionToken.of("random", UUID.randomUUID().toString()), switchInfo, new HashMap<>(8));
                    save(switchSession);
                    return switchSession;
                });
            } else {
                return genSession(switchInfo.getUserId(), switchInfo.getDeviceType(), session.getSessionToken().getGenType(), new HashMap<>(8), null);
            }
        }).orElse(session);
    }

    @Override
    public Optional<ReqTokenInfo> findReqTokenInfoFromClient(String tokenName) {
        throw new RuntimeException();
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        throw new RuntimeException();
    }

    @Override
    public void replaced(Session session) {
        Session.DefaultSession s = Session.from(session);
        s.setReplaced(true);
    }

}
