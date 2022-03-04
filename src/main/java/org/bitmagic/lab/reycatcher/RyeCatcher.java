package org.bitmagic.lab.reycatcher;

import org.bitmagic.lab.reycatcher.ex.NotFoundSessionException;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import java.util.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class RyeCatcher {

    private static final String DEFAULT_DEVICE_TYPE = "DEFAULT_DEVICE";

    //TODO
    private static SessionManager sessionManager ;
    //TODO
    private static LoadMatchInfoService loadMatchInfoService;

    public static SessionToken login(Object id){
        return login(id, DEFAULT_DEVICE_TYPE);
    }

    public static SessionToken login(Object id, String deviceType) {
        Session session = sessionManager.genSession(id, deviceType, "cookie", new HashMap<>(), null);
        sessionManager.save(session);
        SessionContextHolder.setContext(SessionContext.of(session));
        return session.getSessionToken();
    }

    public static boolean isLogin() {
        return Objects.nonNull(getSession());
    }

    public static LoginInfo getLogin() {
        return getSession().getLoginInfo();
    }

    public static Session getSession() {
       return SessionContextHolder.getContext().getSession();
    }

    public static Session getSessionByLogin(Object id) {
        return getSessionByLogin(id, DEFAULT_DEVICE_TYPE);
    }

    public static Session getSessionByLogin(Object id, String deviceType) {
        return sessionManager.findOne(id, deviceType).orElseThrow(NotFoundSessionException::new);
    }

    public static boolean anyMatch(String type, String... args) {
        //TODO 模糊匹配
        Set<String> permissions = listPermission(type);
        return Arrays.stream(args).anyMatch(permissions::contains);
    }

    public static boolean allMatch(String type, String... args) {
        //TODO 模糊匹配
        Set<String> permissions = listPermission(type);
        return Arrays.stream(args).allMatch(permissions::contains);
    }

    public static boolean noneMatch(String type, String... args) {
        //TODO 模糊匹配
        Set<String> permissions = listPermission(type);
        return Arrays.stream(args).noneMatch(permissions::contains);
    }

    public static void check(String type, MatchRelation matchRelation, String... args) {
        boolean flag = true;
        if(MatchRelation.ALL.equals(matchRelation)){
            flag=  allMatch(type, args);
        }else if(MatchRelation.ANY.equals(matchRelation)){
            flag=  anyMatch(type, args);
        }else if(MatchRelation.NONE.equals(matchRelation)){
            flag=  noneMatch(type, args);
        }
        ValidateUtils.checkAuthority(flag, String.join(",", args));
    }

    public static void switchTo(Object id) {
        switchTo(id, DEFAULT_DEVICE_TYPE);
    }

    public static void switchTo(Object id, String deviceType) {
        Session session = sessionManager.findOne(id, deviceType).orElseThrow(NotFoundSessionException::new);
        SessionContextHolder.setContext(SessionContext.of(session));
    }

    public static void logout() {
        sessionManager.remove(getSession());
    }

    public static void kickOut(Object id) {
        kickOut(id, DEFAULT_DEVICE_TYPE);
    }

    public static void kickOut(Object id, String deviceType) {
        sessionManager.remove(getSessionByLogin(id, deviceType));
    }

    private static Set<String> listPermission(String type){
        LoginInfo loginInfo = getSession().getLoginInfo();
       return  new HashSet<>(loadMatchInfoService.loadMatchInfo(loginInfo.getUserId(), loginInfo.getDeviceType()).getOrDefault(type, Collections.emptyList()));
    }
}
