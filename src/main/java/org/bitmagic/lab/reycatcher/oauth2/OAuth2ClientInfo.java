package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yangrd
 * // 有无登录 去认证
 * // 获取token
 * // 获取用户信息
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
    private String userNameAttributeName;
    private String userIdAttributeName;
    private String authorizationSuccessRedirectUri;

//    private Integer accessTokenValiditySeconds;
//    private Integer refreshTokenValiditySeconds;
//    private String checkAccessTokenUri;
//    private String checkRefreshTokenUri;

    public OAuth2ClientInfo() {
        this.scope = new HashSet<>(Arrays.asList("read", "write"));
        this.authorizedGrantTypes = new HashSet<>(Arrays.asList("authorization_code", "refresh_token"));
        this.redirectUri = "http://localhost:8080/oauth/callback";
        this.authorizationUri = "http://localhost:8080/oauth/authorize";
        this.tokenUri = "http://localhost:8080/oauth/token";
        this.userInfoUri = "http://localhost:8080/oauth/userinfo";
        this.userNameAttributeName = "name";
        this.userIdAttributeName = "id";
        this.authorizationSuccessRedirectUri = "/";
    }

}
