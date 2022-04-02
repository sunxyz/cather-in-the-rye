package org.bitmagic.lab.reycatcher.oauth2.model;

import lombok.Data;

/**
 * @author yangrd
 */
@Data
public class RequestTokenInfo {
    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
}
