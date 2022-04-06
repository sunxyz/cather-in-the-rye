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
    private String redirectUrl;
    private Set<String> scope;
    private Set<String> authorizedGrantTypes;

    private String authorizationUrl;
    private String tokenUrl;
    private String userInfoUrl;
    private String userNameAttributeName;
    private String userIdAttributeName;

//    private Integer accessTokenValiditySeconds;
//    private Integer refreshTokenValiditySeconds;
//    private String checkAccessTokenUri;
//    private String checkRefreshTokenUri;

    public OAuth2ClientInfo() {
        this.scope = new HashSet<>(Arrays.asList("read", "write"));
        this.authorizedGrantTypes = new HashSet<>(Arrays.asList("authorization_code", "refresh_token"));
        this.redirectUrl = "http://localhost:8080/oauth/callback";
        this.authorizationUrl = "http://localhost:8080/oauth/authorize";
        this.tokenUrl = "http://localhost:8080/oauth/token";
        this.userInfoUrl = "http://localhost:8080/oauth/userinfo";
        this.userNameAttributeName = "name";
    }

}
