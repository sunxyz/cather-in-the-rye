package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.Data;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ClientInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yangrd
 */
@ConfigurationProperties("rey-catcher.oauth2.sso")
@Data
public class OAuth2ClientSsoConfigurationProperties {

    private OAuth2ClientInfo client;
}
