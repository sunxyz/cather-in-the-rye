package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ClientInfo;
import org.bitmagic.lab.reycatcher.oauth2.helper.RcOauth2Helper;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.store.Oauth2Token;
import org.bitmagic.lab.reycatcher.oauth2.support.Oauth2Support;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author yangrd
 */
@Configuration
@EnableConfigurationProperties(OAuth2ClientSsoConfigurationProperties.class)
public class OAuth2ClientSsoConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> registrationOauth2SsoLoginFilterBean(OAuth2ClientSsoConfigurationProperties properties) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new RcOauth2SsoLoginFilter(properties.getClient()));
        bean.addUrlPatterns("/*");
        bean.setName("oauth2SsoLoginFilterBean");
        bean.setOrder(-100);
        return bean;
    }

    @RequiredArgsConstructor
    static class RcOauth2SsoLoginFilter extends GenericFilter {

        private final OAuth2ClientInfo oAuth2ClientInfo;

        private final RestTemplate restTemplate = new RestTemplate();

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            // 获取code
            if(((HttpServletRequest) servletRequest).getRequestURI().indexOf(oAuth2ClientInfo.getRedirectUrl())==0){
                String code = servletRequest.getParameter("code");
                if(code!=null){
                    // 获取token
                    RequestTokenInfo requestTokenInfo = new RequestTokenInfo()
                            .setCode(code)
                            .setClientId(oAuth2ClientInfo.getClientId())
                            .setClientSecret(oAuth2ClientInfo.getClientSecret())
                            .setGrantType("authorization_code")
                            .setRedirectUri(oAuth2ClientInfo.getRedirectUrl());
                    // 异常直接抛出
                    Oauth2Token oauth2Token = restTemplate.postForObject(oAuth2ClientInfo.getTokenUrl()+Oauth2Support.getTokenUrlParams(requestTokenInfo), null, Oauth2Token.class);
                    if(oauth2Token!=null){
                        // 获取用户信息
                        Map userInfo = restTemplate.getForObject(oAuth2ClientInfo.getUserInfoUrl() + "?access_token" + oauth2Token.getAccessToken(), Map.class);
                        if(userInfo!=null){
                            Object userId = userInfo.get(oAuth2ClientInfo.getUserIdAttributeName());
                            if(userId!=null){
                                RyeCatcher.login(userId);
                            }
                            RyeCatcher.getSession().setAttribute("oauth2UserInfo", userInfo);
                            RyeCatcher.getSession().setAttribute("username", userInfo.get(oAuth2ClientInfo.getUserNameAttributeName()));
                        }
                    }
                }
            }else if(RyeCatcher.isLogin()) {
               filterChain.doFilter(servletRequest, servletResponse);
           }else {
               RcOauth2Helper.redirectToLogin((HttpServletResponse) servletResponse, oAuth2ClientInfo);
           }
        }

        @Override
        public void destroy() {

        }
    }

}
