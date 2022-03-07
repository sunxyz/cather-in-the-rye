package org.bitmagic.lab.reycatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;

import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface Session {

    static Session of(SessionToken sessionToken, LoginInfo loginInfo, Object meta) {
        return DefaultSession.of(sessionToken, loginInfo, meta, System.currentTimeMillis(), System.currentTimeMillis(), ConfigHolder.getSessionTimeOutMillisecond());
    }

    static <T extends Session> T from(Session session){
        return (T) session;
    }

    SessionToken getSessionToken();

    LoginInfo getLoginInfo();

    Object getMeta();

    long getCreationTime();

    long getLastAccessedTime();

    void setMaxInactiveInterval(int var1);

    int getMaxInactiveInterval();

    void setAttribute(String var1, Object var2);

    Object getAttribute(String var1);

    void removeAttribute(String var1);

    default boolean isNeedSave() {
        return ConfigHolder.isNeedSave(getSessionToken().getType());
    }

    default boolean isNeedOutClient() {
        return ConfigHolder.isNeedOutClient(getSessionToken().getType());
    }

    @AllArgsConstructor(staticName = "of")
    @Getter
    class DefaultSession implements Session {

        SessionToken sessionToken;

        LoginInfo loginInfo;

        Object meta;

        long creationTime;

        @Setter
        long lastAccessedTime;

        @Setter
        int maxInactiveInterval;

        @Override
        public void setAttribute(String key, Object val) {
            if(meta instanceof Map){
                ((Map<String, Object>) meta).put(key,val);
            }
        }

        @Override
        public Object getAttribute(String key) {
            if(meta instanceof Map){
               return  ((Map<String, Object>) meta).get(key);
            }
            return null;
        }

        @Override
        public void removeAttribute(String key) {
            if(meta instanceof Map){
                ((Map<String, Object>) meta).remove(key);
            }
        }
    }
}
