package org.bitmagic.lab.reycatcher.oauth2.model;

import lombok.Data;

/**
 * @author yangrd
 */
@Data
public class AuthorizeInfo {
    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String state;
}
