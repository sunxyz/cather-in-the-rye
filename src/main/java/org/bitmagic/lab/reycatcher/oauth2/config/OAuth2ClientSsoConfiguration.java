package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.config.spring.ServletSessionManager;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ClientInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.store.Oauth2Token;
import org.bitmagic.lab.reycatcher.oauth2.support.OAuth2ExceptionUtils;
import org.bitmagic.lab.reycatcher.oauth2.support.Oauth2Support;
import org.bitmagic.lab.reycatcher.utils.Base64Utils;
import org.bitmagic.lab.reycatcher.utils.IdGenerator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author yangrd
 */
@Configuration
@EnableConfigurationProperties(OAuth2ClientSsoConfigurationProperties.class)

@Slf4j
public class OAuth2ClientSsoConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> registrationOauth2SsoLoginFilterBean(OAuth2ClientSsoConfigurationProperties properties) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new RcOauth2SsoLoginFilter(properties.getClient()));
        bean.addUrlPatterns("/*");
        bean.setName("oauth2SsoLoginFilterBean");
        bean.setOrder(-600);
        return bean;
    }

    static class RcOauth2SsoLoginFilter extends HttpFilter {
        private final OAuth2ClientInfo oAuth2ClientInfo;
        private final RestTemplate restTemplate = new RestTemplate();

        public RcOauth2SsoLoginFilter(OAuth2ClientInfo oAuth2ClientInfo) {
            this.oAuth2ClientInfo = oAuth2ClientInfo;
        }

        @Override
        public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            log.debug("uri: {}", servletRequest.getRequestURI());
            Enumeration<String> headerNames = servletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.debug("header: {}", headerName + ":" + servletRequest.getHeader(headerName));
            }
            URI uri = URI.create(oAuth2ClientInfo.getRedirectUri());

            if (RyeCatcher.isLogin()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else if ( uri.getPath().equals(servletRequest.getRequestURI())) {
                String code = servletRequest.getParameter("code");
                if (code != null) {
                    Oauth2Token oauth2Token = getOauth2Token(code);
                    if (oauth2Token != null) {
                        Map<String, Object> userInfo = getUserInfo(oauth2Token);
                        if (userInfo != null) {
                            Object userId = userInfo.get(this.oAuth2ClientInfo.getUserIdAttributeName());
                            Object userName = userInfo.get(this.oAuth2ClientInfo.getUserNameAttributeName());
                            servletRequest.setAttribute(ServletSessionManager.COOKIE_PATH, "/");
                            if (userId != null) {
                                RyeCatcher.login(userId);
                            } else {
                                RyeCatcher.login(userName);
                            }
                            RyeCatcher.getSession().setAttribute("oauth2UserInfo", userInfo);
                            RyeCatcher.getSession().setAttribute("username", userName);
                            RyeCatcher.getSession().setAttribute("userId", userId);
                            RyeCatcher.getSession().setAttribute("oauth2Token", oauth2Token);
                            servletResponse.sendRedirect(this.oAuth2ClientInfo.getAuthorizationSuccessRedirectUri());
                        }
                    }
                }
            } else {
                OAuth2ExceptionUtils.tryOauth2Exception(!oAuth2ClientInfo.getAuthorizedGrantTypes().contains("authorization_code"),"only support authorization_code");
                String state = IdGenerator.genUuid();
                Oauth2Support.redirectToLogin(servletResponse, this.oAuth2ClientInfo, state);
            }
        }

        private Map<String, Object> getUserInfo(Oauth2Token oauth2Token) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", oauth2Token.getTokenType() + " " + oauth2Token.getAccessToken());
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            return restTemplate.exchange(this.oAuth2ClientInfo.getUserInfoUri(), HttpMethod.GET, requestEntity, Map.class).getBody();
        }

        private Oauth2Token getOauth2Token(String code) {
            RequestTokenInfo requestTokenInfo = (new RequestTokenInfo()).setCode(code).setClientId(this.oAuth2ClientInfo.getClientId()).setClientSecret(this.oAuth2ClientInfo.getClientSecret()).setGrantType("authorization_code").setRedirectUri(this.oAuth2ClientInfo.getRedirectUri());
            HttpHeaders headers0 = new HttpHeaders();
            headers0.add("Authorization", "Basic " + Base64Utils.encode(this.oAuth2ClientInfo.getClientId() + ":" + this.oAuth2ClientInfo.getClientSecret()));
            HttpEntity<String> requestEntity0 = new HttpEntity<>(headers0);
            return restTemplate.exchange(this.oAuth2ClientInfo.getTokenUri() + Oauth2Support.getTokenUrlParams(requestTokenInfo), HttpMethod.POST, requestEntity0, Oauth2Token.class).getBody();
        }

        @Override
        public void destroy() {

        }
    }

}
