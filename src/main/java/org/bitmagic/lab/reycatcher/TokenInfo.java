package org.bitmagic.lab.reycatcher;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangrd
 */
public interface TokenInfo {

    static TokenInfo of(String accessToken,Integer expiresIn, String scope,String type){
        return SimpleTokenInfo.of(accessToken, expiresIn, scope, type);
    }

    String getAccessToken();

    Integer getExpiresIn();

    String getScope();

    String getType();

    @Getter
    @AllArgsConstructor(staticName = "of")
    class SimpleTokenInfo implements TokenInfo {
        String accessToken;
        Integer expiresIn;
        String scope;
        String type;
    }
}
