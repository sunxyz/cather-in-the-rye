package org.bitmagic.lab.reycatcher.oauth2.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author yangrd
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class Oauth2TokenInfo {
    final String accessToken;
    final  String refreshToken;
    final String tokenType;
    final long expiresIn;
    final String scope;
    final String userId;
    LocalDateTime createTime = LocalDateTime.now();
}
