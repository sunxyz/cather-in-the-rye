package org.bitmagic.lab.reycatcher.oauth2.config;

import org.bitmagic.lab.reycatcher.oauth2.OAuth2AuthorizationServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author yangrd
 */
@Configuration
public class OAuth2AuthorizationServerConfiguration {

    @ConditionalOnBean(OAuth2AuthorizationServerConfigurer.class)
    @ConditionalOnMissingBean(OAuth2AuthorizationServer.class)
    @Bean
    public OAuth2AuthorizationServer oAuth2AuthorizationController(List<OAuth2AuthorizationServerConfigurer> configurers) {
        OAuth2AuthorizationServerConfigurer.AuthorizationServerConfigurer configInfo = new OAuth2AuthorizationServerConfigurer.AuthorizationServerConfigurer();
        configurers.forEach(configurer -> configurer.configure(configInfo));
        return new OAuth2AuthorizationServer(configurers.iterator().next().getOAuth2ConfigurationInfo(), configInfo.oAuth2TokenStore(), configInfo.oAuth2ApprovalStore(), configInfo.oAuth2UserInfoProvider(), configInfo.loginPath(), configInfo.confirmPath());
    }

    @Bean
    public OAuth2AuthorizationController oAuth2AuthorizationController(OAuth2AuthorizationServer oAuth2AuthorizationServer) {
        return new OAuth2AuthorizationController(oAuth2AuthorizationServer);
    }
}
