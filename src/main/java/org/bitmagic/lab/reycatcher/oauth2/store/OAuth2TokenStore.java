package org.bitmagic.lab.reycatcher.oauth2.store;

public interface OAuth2TokenStore {
    void storeToken(String token, Oauth2Token tokenInfo);
    Oauth2Token getTokenInfo(String token);
    Oauth2Token getTokenInfoByRefreshToken(String refreshToken);
    void removeToken(String token);
}
