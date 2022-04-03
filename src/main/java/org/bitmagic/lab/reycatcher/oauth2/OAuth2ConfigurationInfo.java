package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Builder;

import java.util.List;

/**
 * @author yangrd
 */
@Builder
public class OAuth2ConfigurationInfo {

    private List<OAuth2ClientInfo> clientInfos;

    public OAuth2ClientInfo getOauth2ClientInfo(String clientId) {
        for (OAuth2ClientInfo clientInfo : clientInfos) {
            if (clientInfo.getClientId().equals(clientId)) {
                return (clientInfo);
            }
        }
        return null;
    }
}
