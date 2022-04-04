package org.bitmagic.lab.reycatcher.oauth2;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.Oauth2Token;

import java.util.Objects;

import static org.bitmagic.lab.reycatcher.oauth2.support.OAuth2ExceptionUtils.tryOauth2Exception;

/**
 * @author yangrd
 */
@RequiredArgsConstructor
public class OAuth2ResourceServer {

    private final OAuth2TokenStore tokenStore;

    public void checkScope(String accessToken, String scope) {
        Oauth2Token tokenInfo = tokenStore.getTokenInfo(accessToken);
        tryOauth2Exception(Objects.isNull(tokenInfo), "accessToken not found");
        tryOauth2Exception(!tokenInfo.getScope().contains(scope), "scope not match");
    }

    public String getUserId(String accessToken) {
        Oauth2Token tokenInfo = tokenStore.getTokenInfo(accessToken);
        tryOauth2Exception(Objects.isNull(tokenInfo), "accessToken not found");
        return tokenInfo.getUserId();
    }

    public void checkResourceId(String accessToken, String resourceId) {
        Oauth2Token tokenInfo = tokenStore.getTokenInfo(accessToken);
        tryOauth2Exception(Objects.isNull(tokenInfo), "accessToken not found");
        tryOauth2Exception(!tokenInfo.getResourceIds().contains(resourceId), "resourceId not match");
    }
}
