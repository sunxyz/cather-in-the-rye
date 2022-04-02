package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.SessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.*;
import org.bitmagic.lab.reycatcher.predicates.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
    public CertificationSystemPredicate certificationSystemPredicate(List<CertificationSystemPredicate> certificationSystemPredicates) {
        return new CompositeCertificationSystemPredicate(certificationSystemPredicates);
    }

    @Bean
    public CertificationSystemPredicate cookieCertificationSystemPredicate() {
        return new CookieCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate headerCertificationSystemPredicate() {
        return new HeaderCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate hostCertificationSystemPredicate() {
        return new HostCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate methodCertificationSystemPredicate() {
        return new MethodCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate paramsCertificationSystemPredicate() {
        return new ParamsCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate pathCertificationSystemPredicate() {
        return new PathCertificationSystemPredicate();
    }
}
