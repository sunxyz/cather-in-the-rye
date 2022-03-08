package org.bitmagic.lab.reycatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;

import java.util.Map;
import java.util.UUID;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface Session {

    static Session of(SessionToken sessionToken, LoginInfo loginInfo, Object meta) {
        return DefaultSession.of(UUID.randomUUID().toString(),sessionToken, loginInfo, meta, System.currentTimeMillis(), System.currentTimeMillis(), ConfigHolder.getSessionTimeOutMillisecond());
    }

    static <T extends Session> T from(Session session) {
        return (T) session;
    }

    String getId();

    SessionToken getSessionToken();

    LoginInfo getLoginInfo();

    Object getMeta();

    long getCreationTime();

    long getLastAccessedTime();

    int getMaxInactiveInterval();

    void setMaxInactiveInterval(int var1);

    void setAttribute(String var1, Object var2);

    Object getAttribute(String var1);

    void removeAttribute(String var1);

    @AllArgsConstructor(staticName = "of")
    @Getter
    class DefaultSession implements Session {

        String id;

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
            if (meta instanceof Map) {
                ((Map<String, Object>) meta).put(key, val);
            }
        }

        @Override
        public Object getAttribute(String key) {
            if (meta instanceof Map) {
                return ((Map<String, Object>) meta).get(key);
            }
            return null;
        }

        @Override
        public void removeAttribute(String key) {
            if (meta instanceof Map) {
                ((Map<String, Object>) meta).remove(key);
            }
        }
    }
}
