package org.bitmagic.lab.reycatcher.oauth2.config;

import org.bitmagic.lab.reycatcher.oauth2.OAuth2AuthorizationServerClientInfo;

import java.util.List;
import java.util.function.Function;

/**
 * @author yangrd
 */
public class OAuth2AuthorizationServerClientsConfigurer {

    private final OAuth2AuthorizationServerClientRegistry clientRegistry = new OAuth2AuthorizationServerClientRegistry();

    private Function<String, OAuth2AuthorizationServerClientInfo> clientMapper = clientRegistry::getClient;

    public void registerClient(OAuth2AuthorizationServerClientInfo client) {
        clientRegistry.addClient(client);
    }

    public void registerClients(List<OAuth2AuthorizationServerClientInfo> clients) {
        clients.forEach(clientRegistry::addClient);
    }

    public void setClientMapper(Function<String, OAuth2AuthorizationServerClientInfo> clientMapper) {
        this.clientMapper = clientMapper;
    }

    public OAuth2AuthorizationServerClientInfo getClientInfo(String clientId) {
        return clientMapper.apply(clientId);
    }
}
