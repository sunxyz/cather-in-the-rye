package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author yangrd
 */
@Data
@Accessors(chain  = true)
public class OAuth2AuthorizationServerClientInfo {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private Set<String> scopes;
    private Set<String> grantTypes;
    private Set<String> resourceIds;
    private long accessTokenExpireTime;
    private long refreshTokenExpireTime;
}
