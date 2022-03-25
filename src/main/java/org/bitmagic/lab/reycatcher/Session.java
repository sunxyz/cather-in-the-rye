package org.bitmagic.lab.reycatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.utils.IdGenerator;

import java.util.Map;

import static org.bitmagic.lab.reycatcher.SessionToken.GenTypeCons.SESSION_ID;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface Session {

    static Session of(SessionToken sessionToken, LoginInfo loginInfo, Object meta) {
        return DefaultSession.of(SESSION_ID.equals(sessionToken.getGenType()) ? sessionToken.getToken() : IdGenerator.genUuid(), sessionToken, loginInfo, meta, System.currentTimeMillis(), System.currentTimeMillis(), DynamicRcConfigHolder.getSessionTimeoutMillisecond(),false);
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

    boolean isReplaced();

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

        /**
         * 被取代
         */
        @Setter
        boolean replaced;

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
