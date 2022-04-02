package org.bitmagic.lab.reycatcher.oauth2;

import org.bitmagic.lab.reycatcher.oauth2.model.Oauth2TokenInfo;

public interface OAuth2TokenStore {
    void storeToken(String token, Oauth2TokenInfo tokenInfo);
    Oauth2TokenInfo getTokenInfo(String token);
    Oauth2TokenInfo getTokenInfoByRefreshToken(String refreshToken);
    void removeToken(String token);
}
