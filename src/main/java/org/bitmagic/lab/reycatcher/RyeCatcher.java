package org.bitmagic.lab.reycatcher;

import com.auth0.jwt.interfaces.Claim;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.NoLoginException;
import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;
import org.bitmagic.lab.reycatcher.func.PathMatcher;
import org.bitmagic.lab.reycatcher.support.SessionContext;
import org.bitmagic.lab.reycatcher.support.SessionContextHolder;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public final class RyeCatcher {

    private static final String DEFAULT_DEVICE_TYPE = "DEFAULT_DEVICE";

    private static final SessionManager SESSION_MANAGER = InstanceHolder.getInstance(SessionManager.class);

    private static final AuthMatchInfoProvider MATCH_INFO_PROVIDER = InstanceHolder.getInstance(AuthMatchInfoProvider.class);

    private static final RyeCatcherActionListener ACTION_LISTENER = InstanceHolder.getInstance(RyeCatcherActionListener.class);

    private static final PathMatcher COLON_PATH_MATCHER = InstanceHolder.getInstance("colonPathMatcher", PathMatcher.class);

    private RyeCatcher() {
    }

    public static TokenInfo login(Object id) {
        return login(id, DEFAULT_DEVICE_TYPE);
    }

    public static TokenInfo login(Object id, String deviceType) {
        return login(id, deviceType, Collections.emptyMap());
    }

    public static TokenInfo login(Object id, String deviceType, Map<String, Object> clientExtMeta) {
        Session session = SESSION_MANAGER.genSession(id, deviceType, DynamicRcConfigHolder.getGenTokenType(), new HashMap<>(8), clientExtMeta);
        if (DynamicRcConfigHolder.isSameDriveMutex() && DynamicRcConfigHolder.isNeedSave()) {
            SESSION_MANAGER.findByLoginInfo(id, deviceType).ifPresent(session1 -> {
                SESSION_MANAGER.replaced(session1);
                ACTION_LISTENER.doBeReplaced(DynamicRcConfigHolder.getCertificationSystemId(), session1.getLoginInfo().getUserId(), session1.getLoginInfo().getDeviceType(), session1.getSessionToken());
            });
        }
        if (DynamicRcConfigHolder.isNeedSave()) {
            SESSION_MANAGER.save(session);
        }
        if (DynamicRcConfigHolder.isNeedOutClient()) {
            SESSION_MANAGER.outSession2Client(DynamicRcConfigHolder.getOutClientTokenName(), session);
        }
        SessionContextHolder.setContext(SessionContext.of(session));
        ACTION_LISTENER.doLogin(DynamicRcConfigHolder.getCertificationSystemId(), id, deviceType);
        return getTokenInfo();
    }

    public static boolean isLogin() {
        return findSession().isPresent();
    }

    public static LoginInfo getLogin() {
        return findSession().map(Session::getLoginInfo).orElseThrow(NoLoginException::new);
    }

    public static <T> T getLoginId() {
        return (T) getLogin().getUserId();
    }

    public static TokenInfo getTokenInfo() {
        return SESSION_MANAGER.findReqTokenInfoFromClient(DynamicRcConfigHolder.getOutClientTokenName()).map(reqTokenInfo -> {
            return TokenInfo.of(reqTokenInfo.getValue(), DynamicRcConfigHolder.getSessionTimeoutMillisecond(), DynamicRcConfigHolder.listCertificationSystemPredicate(), reqTokenInfo.getType(), DynamicRcConfigHolder.getOutClientTokenName());
        }).orElseGet(() -> {
            Session session = getSession();
            SessionToken sessionToken = session.getSessionToken();
            return TokenInfo.of(sessionToken.getToken(), session.getMaxInactiveInterval(), DynamicRcConfigHolder.listCertificationSystemPredicate(), SessionToken.GenTypeCons.JWT.equals(sessionToken.getGenType()) ? "Bearer" : null, DynamicRcConfigHolder.getOutClientTokenName());
        });
    }

    public static Session getSession() {
        return findSession().orElseThrow(NotFoundSessionException::new);
    }

    public static Optional<Session> findSession() {
        return SessionContextHolder.getContext().findSession();
    }

    public static Session getSavedSessionByLogin(Object id) {
        return getSavedSessionByLogin(id, DEFAULT_DEVICE_TYPE);
    }

    public static Session getSavedSessionByLogin(Object id, String deviceType) {
        return SESSION_MANAGER.findByLoginInfo(id, deviceType).orElseThrow(NotFoundSessionException::new);
    }

    public static boolean anyMatch(String type, String... authKeys) {
        Collection<String> authorities = listAuthorizedInfo(type);
        return Arrays.stream(authKeys).anyMatch(authKey -> match(authorities, authKey));
    }

    public static boolean allMatch(String type, String... authKeys) {
        Collection<String> authorities = listAuthorizedInfo(type);
        return Arrays.stream(authKeys).allMatch(authKey -> match(authorities, authKey));
    }

    public static boolean noneMatch(String type, String... authKeys) {
        Collection<String> authorities = listAuthorizedInfo(type);
        return Arrays.stream(authKeys).noneMatch(authKey -> match(authorities, authKey));
    }

    public static void check(String type, MatchRelation matchRelation, String... authKeys) {
        ValidateUtils.checkGrant(has(type, matchRelation, authKeys), type + " matchRelation:" + matchRelation + ": " + String.join(",", authKeys));
    }

    public static boolean has(String type, MatchRelation matchRelation, String... authKeys) {
        boolean flag = true;
        if (MatchRelation.ALL.equals(matchRelation)) {
            flag = allMatch(type, authKeys);
        } else if (MatchRelation.ANY.equals(matchRelation)) {
            flag = anyMatch(type, authKeys);
        } else if (MatchRelation.NONE.equals(matchRelation)) {
            flag = noneMatch(type, authKeys);
        }
        return flag;
    }

    public static void checkLogin() {
        findSession().orElseThrow(NoLoginException::new);
    }

    public static void switchTo(Object id) {
        switchTo(id, DEFAULT_DEVICE_TYPE);
    }

    public static void switchTo(Object id, String deviceType) {
        LoginInfo login = getLogin();
        SESSION_MANAGER.switchId(login, LoginInfo.of(id, deviceType));
        ACTION_LISTENER.doSwitch(DynamicRcConfigHolder.getCertificationSystemId(), login.getUserId(), login.getDeviceType(), id, deviceType);
        SessionContextHolder.clear();
    }

    public static void stopSwitch() {
        LoginInfo login = getLogin();
        SESSION_MANAGER.switchId(login, null);
        ACTION_LISTENER.doStopSwitch(DynamicRcConfigHolder.getCertificationSystemId(), login.getUserId(), login.getDeviceType());
        SessionContextHolder.clear();
    }

    public static void logout() {
        Session session = getSession();
        if (DynamicRcConfigHolder.isNeedSave()) {
            SESSION_MANAGER.remove(session);
        }
        if (DynamicRcConfigHolder.isNeedOutClient()) {
            session.setMaxInactiveInterval(0);
            SESSION_MANAGER.outSession2Client(DynamicRcConfigHolder.getOutClientTokenName(), session);
        }
        ACTION_LISTENER.doLogout(DynamicRcConfigHolder.getCertificationSystemId(), session.getLoginInfo().getUserId(), session.getLoginInfo().getDeviceType(), session.getSessionToken());
    }

    public static void kickOut(Object id) {
        kickOut(id, DEFAULT_DEVICE_TYPE);
    }

    public static void kickOut(Object id, String deviceType) {
        Session session = getSavedSessionByLogin(id, deviceType);
        if (DynamicRcConfigHolder.isNeedSave()) {
            SESSION_MANAGER.remove(session);
        }
        ACTION_LISTENER.doKicked(DynamicRcConfigHolder.getCertificationSystemId(), session.getLoginInfo().getUserId(), session.getLoginInfo().getDeviceType(), session.getSessionToken());
    }

    private static Collection<String> listAuthorizedInfo(String type) {
        if (DynamicRcConfigHolder.getGenTokenType().equals(SessionToken.GenTypeCons.JWT) && DynamicRcConfigHolder.isEnableJwtAuthMatchInfoPayload()) {
            Object v = getSession().getAttribute("auth-" + type);
            return Objects.isNull(v) ? Collections.emptyList() : ((Claim) v).asList(String.class);
        }
        LoginInfo loginInfo = getSession().getLoginInfo();
        return MATCH_INFO_PROVIDER.loadAuthMatchInfo(DynamicRcConfigHolder.getCertificationSystemId(), type, loginInfo.getUserId(), loginInfo.getDeviceType());
    }

    private static boolean match(Collection<String> authorities, String authKey) {
        return authorities.stream().anyMatch(auth -> COLON_PATH_MATCHER.match(authKey, auth));
    }
}
