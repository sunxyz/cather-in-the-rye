package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yangrd
 */
public interface OAuth2AuthorizationServerConfigurer extends WebMvcConfigurer {

    void configure(OAuth2AuthorizationServerClientsConfigurer clientsConfigurer);

    void configure(AuthorizationServerConfigurer serverConfigurer);

    @Accessors(fluent = true, chain = true)
    @Getter
    class AuthorizationServerConfigurer {
        private OAuth2TokenStore oAuth2TokenStore = MemoryOAuth2TokenStore.INSTANCE;;
        private OAuth2ApprovalStore oAuth2ApprovalStore = new MemoryOAuth2ApprovalStore();
        private String loginPath = "/oauth/login";
        private String confirmPath = "/oauth/confirm";
    }


}
