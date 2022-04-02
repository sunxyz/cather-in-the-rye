package org.bitmagic.lab.reycatcher.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.AuthMatchInfoProvider;
import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.config.Environment;
import org.bitmagic.lab.reycatcher.config.RyeCatcherBootstrap;
import org.bitmagic.lab.reycatcher.predicates.CertificationSystemPredicate;
import org.bitmagic.lab.reycatcher.support.RyeCatcherServletFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;
import java.util.Collections;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Configuration
@EnableConfigurationProperties(RyeCatcherProperties.class)
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
@Import(SpringRyeCatcherRegister.class)
@Slf4j
public class SpringRyeCatcherAutoConfiguration {


    @Autowired
    public void init(RyeCatcherProperties properties, CertificationSystemPredicate certificationSystemPredicate, SessionManager sessionManager, AuthMatchInfoProvider authMatchInfoProvider, RyeCatcherActionListener ryeCatcherActionListener) {
        Environment environment = Environment.of(sessionManager, authMatchInfoProvider, ryeCatcherActionListener, null, Collections.singleton(certificationSystemPredicate));
        org.bitmagic.lab.reycatcher.config.Configuration configuration = org.bitmagic.lab.reycatcher.config.Configuration.of(environment, properties.getCertificationSystems());
        RyeCatcherBootstrap.getInstance().init(configuration);
    }


    @Bean
    public FilterRegistrationBean<Filter> registrationSessionFilter(SessionManager sessionManager) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new ContextHolderInitFilter(sessionManager));
        bean.addUrlPatterns("/*");
        bean.setName("rcSessionFilter");
        bean.setOrder(-200);
        return bean;
    }

    @ConditionalOnBean(RyeCatcherServletFilter.class)
    @Bean
    public FilterRegistrationBean<Filter> registrationRyeCatcherServletFilter(RyeCatcherServletFilter ryeCatcherServletFilter) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(ryeCatcherServletFilter);
        bean.addUrlPatterns("/*");
        bean.setName("ryeCatcherServletFilter");
        bean.setOrder(-100);
        return bean;
    }

    @Bean
    public RcErrorAttributes rcErrorAttributes() {
        return new RcErrorAttributes();
    }

    @Bean
    public RcWebExceptionHandler rcWebExceptionHandler() {
        return new RcWebExceptionHandler();
    }


}
