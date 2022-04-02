package org.bitmagic.lab.reycatcher.oauth2;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.oauth2.model.Oauth2TokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.UserInfo;
import org.bitmagic.lab.reycatcher.oauth2.support.OAuth2Exception;

import java.util.Objects;

/**
 * @author yangrd
 */
@RequiredArgsConstructor
public class OAuth2ResourceServer {

    private final OAuth2TokenStore tokenStore;

    private final OAuth2UserInfoProvider userInfoProvider;

    /**
     *  access token->userInfo
     * @param accessToken
     * @return
     */
    public UserInfo getUserInfo(String accessToken) {
        Oauth2TokenInfo tokenInfo = tokenStore.getTokenInfo(accessToken);
        if (Objects.isNull(tokenInfo)){
            throw new OAuth2Exception("accessToken not found");
        }
        return userInfoProvider.loadUserInfo(tokenInfo.getUserId()).orElseThrow(() -> new OAuth2Exception("user info not found")) ;
    }

    public void checkScope(String accessToken, String scope) {
        Oauth2TokenInfo tokenInfo = tokenStore.getTokenInfo(accessToken);
        if (Objects.isNull(tokenInfo)){
            throw new OAuth2Exception("accessToken not found");
        }
        if (!tokenInfo.getScope().contains(scope)){
            throw new OAuth2Exception("scope not match");
        }
    }
}
