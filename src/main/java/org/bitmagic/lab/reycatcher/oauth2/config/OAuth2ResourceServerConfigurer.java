package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yangrd
 */
public interface OAuth2ResourceServerConfigurer extends WebMvcConfigurer {

    void configure(ResourceServerConfigurer resourceServerConfigurer);

    @Accessors(fluent = true)
    @Getter
    class ResourceServerConfigurer {
        private String resourceId = "reycatcher";
        private OAuth2TokenStore oAuth2TokenStore = MemoryOAuth2TokenStore.INSTANCE;
    }
}
