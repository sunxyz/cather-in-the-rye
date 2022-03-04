package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface Session {

    static Session of(SessionToken sessionToken, LoginInfo loginInfo, Object meta){
        return UserSession.of(sessionToken, loginInfo, meta);
    }

    SessionToken getSessionToken();

    LoginInfo getLoginInfo();

    Object getMeta();

    @Value(staticConstructor = "of")
    class UserSession implements Session{

        SessionToken sessionToken;

        LoginInfo loginInfo;

        Object meta;

    }
}
