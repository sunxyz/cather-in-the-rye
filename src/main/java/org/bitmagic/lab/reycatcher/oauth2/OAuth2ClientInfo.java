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
public class OAuth2ClientInfo {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private Set<String> scope;
    private Set<String> authorizedGrantTypes;

    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;

//    private Integer accessTokenValiditySeconds;
//    private Integer refreshTokenValiditySeconds;
//    private String checkAccessTokenUri;
//    private String checkRefreshTokenUri;

    public OAuth2ClientInfo() {
        this.scope = new HashSet<>(Arrays.asList("read", "write"));
        this.authorizedGrantTypes = new HashSet<>(Arrays.asList("authorization_code", "refresh_token"));
        this.redirectUri = "http://localhost:8080/oauth2/callback";
        this.authorizationUri = "http://localhost:8080/oauth2/authorize";
        this.tokenUri = "http://localhost:8080/oauth2/token";
        this.userInfoUri = "http://localhost:8080/oauth2/userinfo";
    }

}
