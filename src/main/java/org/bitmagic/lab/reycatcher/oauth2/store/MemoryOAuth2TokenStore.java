package org.bitmagic.lab.reycatcher.oauth2.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangrd
 */
public class MemoryOAuth2TokenStore implements OAuth2TokenStore {

    public static final OAuth2TokenStore INSTANCE = new MemoryOAuth2TokenStore();

    private final Map<String, Oauth2Token> tokenMap = new ConcurrentHashMap<>(32);
    private final Map<String, String> refreshTokenMap = new ConcurrentHashMap<>(32);
    //todo  clear expired token

    @Override
    public void storeToken(String token, Oauth2Token tokenInfo) {
        tokenMap.put(token, tokenInfo);
        refreshTokenMap.put(tokenInfo.getRefreshToken(), token);
    }

    @Override
    public Oauth2Token getTokenInfo(String token) {
        return tokenMap.get(token);
    }

    @Override
    public Oauth2Token getTokenInfoByRefreshToken(String refreshToken) {
        String token = refreshTokenMap.get(refreshToken);
        if (token != null) {
            return tokenMap.get(token);
        }
        return null;
    }

    @Override
    public void removeToken(String token) {
        Oauth2Token tokenInfo = tokenMap.remove(token);
        if (tokenInfo != null) {
            refreshTokenMap.remove(tokenInfo.getRefreshToken());
        }
    }
}
