package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ConfigurationInfo;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2UserInfoProvider;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public interface OAuth2AuthorizationServerConfigurer extends WebMvcConfigurer {

    OAuth2ConfigurationInfo getOAuth2ConfigurationInfo();

    void configurer(OAuth2AuthorizationServerConfigInfo auth2AuthorizationServerConfigInfo);

    @Accessors(fluent = true, chain = true)
    @Getter
    class OAuth2AuthorizationServerConfigInfo {
        private OAuth2TokenStore oAuth2TokenStore = MemoryOAuth2TokenStore.INSTANCE;;
        private OAuth2ApprovalStore oAuth2ApprovalStore = new MemoryOAuth2ApprovalStore();
        private OAuth2UserInfoProvider oAuth2UserInfoProvider;
        private String loginPath = "/oauth2/login";
        private String confirmPath = "/oauth2/confirm";
    }


}
