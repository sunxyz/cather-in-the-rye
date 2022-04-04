package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashSet;
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

    public OAuth2AuthorizationServerClientInfo() {
        super();
    }

    public OAuth2AuthorizationServerClientInfo scopes(String... scopes) {
        this.scopes = new HashSet<>(Arrays.asList(scopes));
        return this;
    }

    public OAuth2AuthorizationServerClientInfo grantTypes(String... grantTypes) {
        this.grantTypes = new HashSet<>(Arrays.asList(grantTypes));
        return this;
    }

    public OAuth2AuthorizationServerClientInfo resourceIds(String... resourceIds) {
        this.resourceIds = new HashSet<>(Arrays.asList(resourceIds));
        this.resourceIds.add("rye-catcher");
        return this;
    }
}
