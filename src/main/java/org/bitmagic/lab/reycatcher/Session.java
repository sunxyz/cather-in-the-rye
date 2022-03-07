package org.bitmagic.lab.reycatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface Session {

    static Session of(SessionToken sessionToken, LoginInfo loginInfo, Object meta) {
        return UserSession.of(sessionToken, loginInfo, meta, System.currentTimeMillis() + Config.getSessionTimeOutMillisecond());
    }

    SessionToken getSessionToken();

    LoginInfo getLoginInfo();

    Object getMeta();

    long getTimeOutMillisecond();

    void renewalTimeOutMillisecond();

    default boolean isNeedSave() {
        return Config.isNeedSave(getSessionToken().getType());
    }

    default boolean isNeedOutClient() {
        return Config.isNeedOutClient(getSessionToken().getType());
    }

    @AllArgsConstructor(staticName = "of")
    @Getter
    class UserSession implements Session {

        SessionToken sessionToken;

        LoginInfo loginInfo;

        Object meta;

        long timeOutMillisecond;

        @Override
        public void renewalTimeOutMillisecond() {
            if(isNeedSave()){
                this.timeOutMillisecond = System.currentTimeMillis() + Config.getSessionTimeOutMillisecond();
            }
        }
    }
}
