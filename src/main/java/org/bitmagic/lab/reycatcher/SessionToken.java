package org.bitmagic.lab.reycatcher;

import lombok.Value;
import org.bitmagic.lab.reycatcher.support.AuthorizationInfo;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface SessionToken {

    interface TokenTypeCons{
         String JWT = "jwt";
         String SESSION_ID = "session_id";
    }

    static SessionToken of(String type, String token){
        return SimpleSessionToken.of(type,token);
    }

    String getType();

    String getToken();

    default AuthorizationInfo getAuthorizationInfo(){
        return TokenParseUtils.findAuthorizationInfo(getToken()).orElseThrow(RuntimeException::new);
    }

    @Value(staticConstructor = "of")
    class SimpleSessionToken implements SessionToken {
        String type;
        String token;
    }


}
