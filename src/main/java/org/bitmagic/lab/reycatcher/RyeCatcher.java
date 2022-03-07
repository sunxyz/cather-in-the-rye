package org.bitmagic.lab.reycatcher;

import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.NoLoginException;
import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;
import org.springframework.util.AntPathMatcher;

import java.util.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class RyeCatcher {

    private static final String DEFAULT_DEVICE_TYPE = "DEFAULT_DEVICE";

    private static final SessionManager SESSION_MANAGER = InstanceHolder.getInstance(SessionManager.class);

    private static final LoadMatchInfoService loadMatchInfoService = InstanceHolder.getInstance(LoadMatchInfoService.class);

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher(":");

    public static SessionToken login(Object id) {
        return login(id, DEFAULT_DEVICE_TYPE);
    }

    public static SessionToken login(Object id, String deviceType) {
        Session session = SESSION_MANAGER.genSession(id, deviceType, ConfigHolder.getGenTokenType(), new HashMap<>(), null);
        if (session.isNeedSave()) {
            SESSION_MANAGER.save(session);
        }
        if (session.isNeedOutClient()) {
            SESSION_MANAGER.outSession2Client(ConfigHolder.getTokenName(), session);
        }
        SessionContextHolder.setContext(SessionContext.ofNullable(session));
        return session.getSessionToken();
    }

    public static boolean isLogin() {
       return findSession().isPresent();
    }

    public static LoginInfo getLogin() {
       return findSession().map(Session::getLoginInfo).orElseThrow(NoLoginException::new);
    }

    public static <T> T getLoginId(){
        return (T)getLogin().getUserId();
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
        return SESSION_MANAGER.findOne(id, deviceType).orElseThrow(NotFoundSessionException::new);
    }

    public static boolean anyMatch(String type, String... authKeys) {
        Collection<String> authorities = listAuthorities(type);
        return Arrays.stream(authKeys).anyMatch(authKey -> match(authorities, authKey));
    }

    public static boolean allMatch(String type, String... authKeys) {
        Collection<String> authorities = listAuthorities(type);
        return Arrays.stream(authKeys).allMatch(authKey -> match(authorities, authKey));
    }

    public static boolean noneMatch(String type, String... authKeys) {
        Collection<String> authorities = listAuthorities(type);
        return Arrays.stream(authKeys).noneMatch(authKey -> match(authorities, authKey));
    }

    public static void check(String type, MatchRelation matchRelation, String... authKeys) {
        boolean flag = true;
        if (MatchRelation.ALL.equals(matchRelation)) {
            flag = allMatch(type, authKeys);
        } else if (MatchRelation.ANY.equals(matchRelation)) {
            flag = anyMatch(type, authKeys);
        } else if (MatchRelation.NONE.equals(matchRelation)) {
            flag = noneMatch(type, authKeys);
        }
        ValidateUtils.checkAuthority(flag, String.join(",", authKeys));
    }

    public static void checkLogin(){
        findSession().orElseThrow(NoLoginException::new);
    }

    public static void switchTo(Object id) {
        switchTo(id, DEFAULT_DEVICE_TYPE);
    }

    public static void switchTo(Object id, String deviceType) {
        Session session = getSavedSessionByLogin(id, deviceType);
        SessionContextHolder.setContext(SessionContext.ofNullable(session));
    }

    public static void logout() {
        Session session = getSession();
        if (session.isNeedSave()) {
            SESSION_MANAGER.remove(session);
        }
        if(session.isNeedOutClient()){
            session.setMaxInactiveInterval(0);
            SESSION_MANAGER.outSession2Client(ConfigHolder.getTokenName(), session);
        }
    }

    public static void kickOut(Object id) {
        kickOut(id, DEFAULT_DEVICE_TYPE);
    }

    public static void kickOut(Object id, String deviceType) {
        Session session = getSavedSessionByLogin(id, deviceType);
        if (session.isNeedSave()) {
            SESSION_MANAGER.remove(session);
        }
    }

    private static Collection<String> listAuthorities(String type) {
        LoginInfo loginInfo = getSession().getLoginInfo();
        return loadMatchInfoService.loadMatchInfo(loginInfo.getUserId(), loginInfo.getDeviceType()).getOrDefault(type, Collections.emptyList());
    }

    private static boolean match(Collection<String> authorities, String authKey) {
        return authorities.stream().anyMatch(auth -> ANT_PATH_MATCHER.match(authKey, auth));
    }
}
