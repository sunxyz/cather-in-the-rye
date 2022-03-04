package org.bitmagic.lab.reycatcher;

import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;
import org.bitmagic.lab.reycatcher.utils.SpringContextHolder;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class RyeCatcher {

    private static final String DEFAULT_DEVICE_TYPE = "DEFAULT_DEVICE";


    private static final SessionManager SESSION_MANAGER = SpringContextHolder.getBean(SessionManager.class);

    private static final LoadMatchInfoService loadMatchInfoService = SpringContextHolder.getBean(LoadMatchInfoService.class);

    private static void initSession() {
        SessionToken sessionToken = SESSION_MANAGER.findSessionTokenFromClient("Authorization").orElseThrow(NotFoundSessionException::new);
        Session session = null;
        String tokenType = sessionToken.getType();
        if (tokenType.equals("cookie")) {
            session = SESSION_MANAGER.findByToken(sessionToken).orElseThrow(NotFoundSessionException::new);
        } else if (tokenType.equals("jwt-token")) {
            LoginInfo loginInfo = null;// jwt->login-info
            Object meta = null; //jwt->login-info
            session = Session.of(sessionToken, loginInfo, meta);
        }
        SessionContextHolder.setContext(SessionContext.of(session));
    }

    public static SessionToken login(Object id) {
       return login(id, DEFAULT_DEVICE_TYPE);
    }

    public static SessionToken login(Object id, String deviceType) {
        Session session = SESSION_MANAGER.genSession(id, deviceType, "cookie", new HashMap<>(), null);
        if (session.getSessionToken().getType().equals("cookie")) {
            SESSION_MANAGER.save(session);
            SESSION_MANAGER.outSession2Client("Authorization", session);
        }
        SessionContextHolder.setContext(SessionContext.of(session));
        return session.getSessionToken();
    }

    public static boolean isLogin() {
        try {
            getSession();
        }catch (NotFoundSessionException e){
            return false;
        }
        return true;
    }

    public static LoginInfo getLogin() {
        return getSession().getLoginInfo();
    }

    public static Session getSession() {
        // cache
        initSession();
        return SessionContextHolder.getContext().getSession();
    }

    public static Session getSessionByLogin(Object id) {
        return getSessionByLogin(id, DEFAULT_DEVICE_TYPE);
    }

    public static Session getSessionByLogin(Object id, String deviceType) {
        return SESSION_MANAGER.findOne(id, deviceType).orElseThrow(NotFoundSessionException::new);
    }

    public static boolean anyMatch(String type, String... args) {
        Collection<String> authorities = listAuthorities(type);
        return Arrays.stream(args).anyMatch(authKey -> match(authorities, authKey));
    }

    public static boolean allMatch(String type, String... args) {
        Collection<String> authorities = listAuthorities(type);
        return Arrays.stream(args).allMatch(authKey -> match(authorities, authKey));
    }

    public static boolean noneMatch(String type, String... args) {
        Collection<String> authorities = listAuthorities(type);
        return Arrays.stream(args).noneMatch(authKey -> match(authorities, authKey));
    }

    public static void check(String type, MatchRelation matchRelation, String... args) {
        boolean flag = true;
        if (MatchRelation.ALL.equals(matchRelation)) {
            flag = allMatch(type, args);
        } else if (MatchRelation.ANY.equals(matchRelation)) {
            flag = anyMatch(type, args);
        } else if (MatchRelation.NONE.equals(matchRelation)) {
            flag = noneMatch(type, args);
        }
        ValidateUtils.checkAuthority(flag, String.join(",", args));
    }

    public static void switchTo(Object id) {
        switchTo(id, DEFAULT_DEVICE_TYPE);
    }

    public static void switchTo(Object id, String deviceType) {
        Session session = SESSION_MANAGER.findOne(id, deviceType).orElseThrow(NotFoundSessionException::new);
        SessionContextHolder.setContext(SessionContext.of(session));
    }

    public static void logout() {
        SESSION_MANAGER.remove(getSession());
    }

    public static void kickOut(Object id) {
        kickOut(id, DEFAULT_DEVICE_TYPE);
    }

    public static void kickOut(Object id, String deviceType) {
        SESSION_MANAGER.remove(getSessionByLogin(id, deviceType));
    }

    private static Collection<String> listAuthorities(String type) {
        LoginInfo loginInfo = getSession().getLoginInfo();
        return loadMatchInfoService.loadMatchInfo(loginInfo.getUserId(), loginInfo.getDeviceType()).getOrDefault(type, Collections.emptyList());
    }

    private static boolean match(Collection<String> authorities, String authKey) {
        //TODO ant match
        return authorities.stream().anyMatch(auth -> {
            Pattern compile = Pattern.compile(auth);
            return compile.matcher(authKey).matches();
        });
    }
}
