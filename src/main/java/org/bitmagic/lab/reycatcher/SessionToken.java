package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionToken {

    interface TokenTypeCons{
         String JWT_TOKEN = "jwt_token";
         String COOKIE = "cookie";
    }

    static SessionToken of(String type, String token){
        return SimpleSessionToken.of(type,token);
    }

    String getType();

    String getToken();

    @Value(staticConstructor = "of")
    class SimpleSessionToken implements SessionToken {
        String type;
        String token;
    }


}
