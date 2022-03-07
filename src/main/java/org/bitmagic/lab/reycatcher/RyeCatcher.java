package org.bitmagic.lab.reycatcher;

import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class RyeCatcher {

    private static final String DEFAULT_DEVICE_TYPE = "DEFAULT_DEVICE";

    private static final SessionManager SESSION_MANAGER = InstanceHolder.getInstance(SessionManager.class);

    private static final LoadMatchInfoService loadMatchInfoService = InstanceHolder.getInstance(LoadMatchInfoService.class);

    public static SessionToken login(Object id) {
       return login(id, DEFAULT_DEVICE_TYPE);
    }

    public static SessionToken login(Object id, String deviceType) {
        Session session = SESSION_MANAGER.genSession(id, deviceType, Config.getGenTokenType(), new HashMap<>(), null);
        if (session.isNeedSave()) {
            SESSION_MANAGER.save(session);
        }
        if(session.isNeedOutClient()){
            SESSION_MANAGER.outSession2Client(Config.getTokenName(), session);
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
        return SessionContextHolder.getContext().getSession();
    }

    public static Session getSavedSessionByLogin(Object id) {
        return getSavedSessionByLogin(id, DEFAULT_DEVICE_TYPE);
    }

    public static Session getSavedSessionByLogin(Object id, String deviceType) {
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
        Session session = getSavedSessionByLogin(id, deviceType);
        SessionContextHolder.setContext(SessionContext.of(session));
    }

    public static void logout() {
        //TODO
        SESSION_MANAGER.remove(getSession());
    }

    public static void kickOut(Object id) {
        kickOut(id, DEFAULT_DEVICE_TYPE);
    }

    public static void kickOut(Object id, String deviceType) {
        //TODO
        SESSION_MANAGER.remove(getSavedSessionByLogin(id, deviceType));
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
