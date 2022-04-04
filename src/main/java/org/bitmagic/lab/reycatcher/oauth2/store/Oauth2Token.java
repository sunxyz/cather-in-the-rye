package org.bitmagic.lab.reycatcher.oauth2.store;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author yangrd
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class Oauth2Token {
    final String accessToken;
    final String refreshToken;
    final String tokenType;
    final long expiresIn;
    final String scope;
    final String userId;
    final Set<String> resourceIds;
    final long refreshTokenExpiresIn;
    LocalDateTime createTime = LocalDateTime.now();

    public Oauth2Token clone(String accessToken) {
        return Oauth2Token.of(accessToken, refreshToken, tokenType, expiresIn, scope, userId, resourceIds, refreshTokenExpiresIn);
    }
}
