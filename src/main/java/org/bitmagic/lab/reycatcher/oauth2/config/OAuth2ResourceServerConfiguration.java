package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;
import org.bitmagic.lab.reycatcher.oauth2.helper.RcOauth2CheckHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author yangrd
 */
@Configuration
public class OAuth2ResourceServerConfiguration {

    @Bean
    @ConditionalOnBean(OAuth2ResourceServerConfigurer.class)
    public OAuth2ResourceServerConfigurer.ResourceServerConfigurer resourceServerConfigurer(List<OAuth2ResourceServerConfigurer> configurers){
        OAuth2ResourceServerConfigurer.ResourceServerConfigurer configInfo = new OAuth2ResourceServerConfigurer.ResourceServerConfigurer();
        configurers.forEach(configurer -> configurer.configure(configInfo));
        return configInfo;
    }


    @Bean
    @ConditionalOnBean(OAuth2ResourceServerConfigurer.ResourceServerConfigurer.class)
    @ConditionalOnMissingBean(OAuth2ResourceServer.class)
    public OAuth2ResourceServer oAuth2AuthorizationController(OAuth2ResourceServerConfigurer.ResourceServerConfigurer configInfo){
        return new OAuth2ResourceServer(configInfo.oAuth2TokenStore());
    }

    @Bean
    @ConditionalOnBean(OAuth2ResourceServerConfigurer.ResourceServerConfigurer.class)
    public FilterRegistrationBean<Filter> registrationSessionFilter(OAuth2ResourceServerConfigurer.ResourceServerConfigurer configInfo) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new OAuth2ResourceServerCheckerFilter(configInfo.resourceId()));
        bean.addUrlPatterns("/*");
        bean.setName("OAuth2ResourceServerCheckerFilter");
        bean.setOrder(-100);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2ResourceServerConfigurer.ResourceServerConfigurer.class)
    public FilterRegistrationBean<Filter> registrationSessionFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new OAuth2ResourceServerCheckerFilter("rye-catcher"));
        bean.addUrlPatterns("/*");
        bean.setName("oAuth2ResourceServerCheckerFilter");
        bean.setOrder(-100);
        return bean;
    }

    @RequiredArgsConstructor
    static class OAuth2ResourceServerCheckerFilter extends GenericFilter {

        private final String resourceId;

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            if(Objects.nonNull(resourceId) ){
                RcOauth2CheckHelper.checkResourceId(resourceId);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {

        }
    }
}
