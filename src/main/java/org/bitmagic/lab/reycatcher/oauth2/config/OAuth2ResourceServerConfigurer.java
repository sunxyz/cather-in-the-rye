package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;

public interface OAuth2ResourceServerConfigurer {

    void configurer(OAuth2ResourceServerConfigInfo configInfo);

    @Accessors(fluent = true)
    @Getter
    class OAuth2ResourceServerConfigInfo {
        private OAuth2TokenStore oAuth2TokenStore = MemoryOAuth2TokenStore.INSTANCE;
    }
}
