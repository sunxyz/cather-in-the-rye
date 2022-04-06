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
        this.scopes = new HashSet<>();
        this.grantTypes = new HashSet<>();
        this.resourceIds = new HashSet<>();
        this.accessTokenExpireTime = 3600;
        this.refreshTokenExpireTime = 2592000;
    }

    public OAuth2AuthorizationServerClientInfo(String clientId, String clientSecret, String redirectUri, String[] scopes, String[] grantTypes, String[] resourceIds, long accessTokenExpireTime, long refreshTokenExpireTime) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scopes = new HashSet<>(Arrays.asList(scopes));
        this.grantTypes = new HashSet<>(Arrays.asList(grantTypes));
        this.resourceIds = new HashSet<>(Arrays.asList(resourceIds));
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
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
