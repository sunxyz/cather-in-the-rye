package org.bitmagic.lab.reycatcher.oauth2.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yangrd
 */
@Data
@Accessors(chain = true)
public class RequestTokenInfo {
    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
}
