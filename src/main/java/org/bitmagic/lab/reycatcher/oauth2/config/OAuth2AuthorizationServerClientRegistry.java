package org.bitmagic.lab.reycatcher.oauth2.config;

import org.bitmagic.lab.reycatcher.oauth2.OAuth2AuthorizationServerClientInfo;

import java.util.HashMap;
import java.util.Map;

public class OAuth2AuthorizationServerClientRegistry {

    private Map<String, OAuth2AuthorizationServerClientInfo> clients = new HashMap<>(10);

    public void addClient(OAuth2AuthorizationServerClientInfo clientInfo) {
        clients.put(clientInfo.getClientId(), clientInfo);
    }

    public OAuth2AuthorizationServerClientInfo getClient(String clientId) {
        return clients.get(clientId);
    }
}
