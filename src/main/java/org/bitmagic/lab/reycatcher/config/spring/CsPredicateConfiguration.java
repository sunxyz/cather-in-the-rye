package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.predicates.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @author yangrd
 */
@Configuration
public class CsPredicateConfiguration {

    @Bean
    @Primary
    public CertificationSystemPredicate certificationSystemPredicate(List<CertificationSystemPredicate> certificationSystemPredicates){
        return new CompositeCertificationSystemPredicate(certificationSystemPredicates);
    }

    @Bean
    public CertificationSystemPredicate cookieCertificationSystemPredicate(){
        return new CookieCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate headerCertificationSystemPredicate(){
        return new HeaderCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate hostCertificationSystemPredicate(){
        return new HostCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate methodCertificationSystemPredicate(){
        return new MethodCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate paramsCertificationSystemPredicate(){
        return new ParamsCertificationSystemPredicate();
    }

    @Bean
    public CertificationSystemPredicate pathCertificationSystemPredicate(){
        return new PathCertificationSystemPredicate();
    }
}
