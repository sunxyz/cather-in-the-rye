package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.Builder;
import lombok.Getter;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2Configuration;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2UserInfoProvider;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.MemoryOAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

public interface OAuth2AuthorizationServerConfigurer extends WebMvcConfigurer {

    OAuth2Configuration configuration();

    OAuth2AuthorizationServerConfigInfo configuration(OAuth2Configuration configuration);

    @Builder
    @Getter
    class OAuth2AuthorizationServerConfigInfo {
        private OAuth2Configuration oAuth2Configuration;
        private OAuth2TokenStore oAuth2TokenStore;
        private OAuth2ApprovalStore oAuth2ApprovalStore;
        private OAuth2UserInfoProvider oAuth2UserInfoProvider;
        private String loginPath;
        private String confirmPath;
        public OAuth2AuthorizationServerConfigInfo(OAuth2Configuration oAuth2Configuration, OAuth2TokenStore oAuth2TokenStore, OAuth2ApprovalStore oAuth2ApprovalStore, OAuth2UserInfoProvider oAuth2UserInfoProvider, String loginPath, String confirmPath) {
            this.oAuth2Configuration = oAuth2Configuration;
            this.oAuth2TokenStore = Objects.isNull(oAuth2TokenStore) ? new MemoryOAuth2TokenStore() : oAuth2TokenStore;
            this.oAuth2ApprovalStore = Objects.isNull(oAuth2ApprovalStore) ? new MemoryOAuth2ApprovalStore() : oAuth2ApprovalStore;
            this.oAuth2UserInfoProvider = oAuth2UserInfoProvider;
            this.loginPath = Objects.isNull(loginPath) ? "/oauth2/login" : loginPath;
            this.confirmPath = Objects.isNull(confirmPath) ? "/oauth2/confirm" : confirmPath;
        }
    }


}
