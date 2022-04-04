package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author yangrd
 */
@Data
@Builder
public class OAuth2AuthorizationServerClientInfo {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private Set<String> scopes;
    private Set<String> grantTypes;
    private long accessTokenExpireTime;
}
