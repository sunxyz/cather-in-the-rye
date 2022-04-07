package org.bitmagic.lab.reycatcher.oauth2.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author yangrd
 */
@Getter
@NoArgsConstructor
public class Oauth2Token {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("refresh_token")
    String refreshToken;
    @JsonProperty("token_type")
    String tokenType;
    @JsonProperty("expires_in")
    long expiresIn;
    @JsonProperty("scope")
    String scope;
    @JsonProperty("user_id")
    String userId;
    @JsonProperty("resource_ids")
    Set<String> resourceIds;
    @JsonIgnore
    long refreshTokenExpiresIn;
    @JsonIgnore
    LocalDateTime createdTime = LocalDateTime.now();

    Oauth2Token(String accessToken, String refreshToken, String tokenType, long expiresIn, String scope, String userId, Set<String> resourceIds, long refreshTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.userId = userId;
        this.resourceIds = resourceIds;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public static Oauth2Token of(String accessToken, String refreshToken, String tokenType, long expiresIn, String scope, String userId, Set<String> resourceIds, long refreshTokenExpiresIn) {
        return new Oauth2Token(accessToken, refreshToken, tokenType, expiresIn, scope, userId, resourceIds, refreshTokenExpiresIn);
    }

    public Oauth2Token clone(String accessToken) {
        return Oauth2Token.of(accessToken, refreshToken, tokenType, expiresIn, scope, userId, resourceIds, refreshTokenExpiresIn);
    }
}
