package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.impl.*;
import org.bitmagic.lab.reycatcher.predicate.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;
import java.util.List;

/**
 * @author yangrd
 */
@Configuration
public class SpringRyeCatcherRegister {

    @ConditionalOnMissingBean(SessionRepository.class)
    @Bean
    public SessionRepository sessionRepository() {
        return new MemorySessionRepository();
    }

    @Bean
    public SessionTokenGenFactory jwtTokenGenService() {
        return new JwtSessionTokenGenFactory();
    }

    @Bean
    public SessionTokenGenFactory cookieTokenGenService() {
        return new SessionIdSessionTokenGenFactory();
    }

    @ConditionalOnMissingBean(AuthMatchInfoProvider.class)
    @Bean
    public AuthMatchInfoProvider authMatchInfoProvider() {
        return (certificationSystemId, userId, deviceType) -> Collections.emptyMap();
    }

    @Bean
    @Primary
    public SessionTokenGenFactory tokenGenService(List<SessionTokenGenFactory> tokenGenServices) {
        return new CompositeSessionTokenGenFactory(tokenGenServices);
    }

    @ConditionalOnMissingBean(SessionManager.class)
    @Bean
    public SessionManager sessionManager(SessionRepository sessionRepository, SessionTokenGenFactory tokenGenService) {
        return new ServletSessionManager(sessionRepository, tokenGenService);
    }

    @ConditionalOnMissingBean(RyeCatcherActionListener.class)
    @Bean
    public RyeCatcherActionListener ryeCatcherListener() {
        return new DefaultRyeCatcherActionListener();
    }

    //----CertificationSystemPredicate---//

    @Bean
    @Primary
    public HttpRequestPredicate certificationSystemPredicate(List<HttpRequestPredicate> httpRequestPredicates) {
        return new CompositeHttpRequestPredicate(httpRequestPredicates);
    }

    @Bean
    public HttpRequestPredicate cookieCertificationSystemPredicate() {
        return new CookieHttpRequestPredicate();
    }

    @Bean
    public HttpRequestPredicate headerCertificationSystemPredicate() {
        return new HeaderHttpRequestPredicate();
    }

    @Bean
    public HttpRequestPredicate hostCertificationSystemPredicate() {
        return new HostHttpRequestPredicate();
    }

    @Bean
    public HttpRequestPredicate methodCertificationSystemPredicate() {
        return new MethodHttpRequestPredicate();
    }

    @Bean
    public HttpRequestPredicate paramsCertificationSystemPredicate() {
        return new ParamsHttpRequestPredicate();
    }

    @Bean
    public HttpRequestPredicate pathCertificationSystemPredicate() {
        return new PathHttpRequestPredicate();
    }
}
