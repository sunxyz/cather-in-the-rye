package org.bitmagic.lab.reycatcher.oauth2.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yangrd
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OAuth2ClientSsoConfiguration.class)
public @interface EnableOAuth2Sso {
}
