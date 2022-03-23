package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionToken {

    interface TokenTypeCons{
         String JWT = "jwt";
         String SESSION_ID = "session_id";
    }

    static SessionToken of(String genType, String token){
        return SimpleSessionToken.of(genType,token);
    }

    String getGenType();

    String getToken();

    @Value(staticConstructor = "of")
    class SimpleSessionToken implements SessionToken {
        String genType;
        String token;
    }


}
