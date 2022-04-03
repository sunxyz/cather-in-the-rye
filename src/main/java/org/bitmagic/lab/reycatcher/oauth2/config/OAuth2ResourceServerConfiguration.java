package org.bitmagic.lab.reycatcher.oauth2.config;

import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author yangrd
 */
@Configuration
public class OAuth2ResourceServerConfiguration {

    @ConditionalOnBean(OAuth2ResourceServerConfigurer.class)
    @ConditionalOnMissingBean(OAuth2ResourceServer.class)
    @Bean
    public OAuth2ResourceServer oAuth2AuthorizationController(List<OAuth2ResourceServerConfigurer> configurers) {
        OAuth2ResourceServerConfigurer.ResourceServerConfigurer configInfo = new OAuth2ResourceServerConfigurer.ResourceServerConfigurer();
        configurers.forEach(configurer -> configurer.configure(configInfo));
        return new OAuth2ResourceServer(configInfo.oAuth2TokenStore());
    }

}
