package org.bitmagic.lab.reycatcher.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.impl.CompositeSessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.JwtSessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.MemorySessionRepository;
import org.bitmagic.lab.reycatcher.impl.SessionIdSessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.utils.SpringContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Configuration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties(RyeCatcherProperties.class)

@Slf4j
public class SpringRyeCatcherConfiguration implements ApplicationContextAware {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final RyeCatcherProperties.CertificationSystemDefine DEFAULT_CERTIFICATION_SYSTEM_INFO = RyeCatcherProperties.CertificationSystemDefine.of("default-id", Arrays.asList(""), null, SessionToken.GenTypeCons.SESSION_ID, "JSESSIONID", 30 * 60 * 1000, true, true, true);

    public void init(RyeCatcherProperties properties) {
        ConfigHolder.delegate = () -> {
            if (Objects.isNull(properties.getCertificationSystems())) {
                return DEFAULT_CERTIFICATION_SYSTEM_INFO;
            }
            String requestUri = RcRequestContextHolder.getContext().getRequest().getRequestURI();
            List<RyeCatcherProperties.CertificationSystemDefine> systemDefines = properties.getCertificationSystems().stream().filter(o -> ANT_PATH_MATCHER.match(o.getPredicates().get(0).replace("Path=", ""), requestUri)).collect(Collectors.toList());
            if (systemDefines.size() != 1) {
                throw new IllegalStateException("systemDefines == 1");
            }
            return systemDefines.iterator().next();
        };
        InstanceHolder.delegate =c->{
            try {
                return SpringContextHolder.getBean(c);
            }catch (BeansException beansException){
                log.warn("BeansException msg:{}",beansException.getMessage());
                return null;
            }
        };
    }

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

    @Bean
    public SessionManager sessionManager(SessionRepository sessionRepository, SessionTokenGenFactory tokenGenService) {
        return new ServletSessionManager(sessionRepository, tokenGenService);
    }

    @ConditionalOnMissingBean(RyeCatcherActionListener.class)
    @Bean
    public RyeCatcherActionListener ryeCatcherListener() {
        return new DefaultRyeCatcherActionListener();
    }

    @Bean
    @Order(Integer.MAX_VALUE)
    public FilterRegistrationBean<Filter> registrationSessionFilter(SessionManager sessionManager) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new ContextHolderInitFilter(sessionManager));
        bean.addUrlPatterns("/*");
        bean.setName("registrationSessionFilter");
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

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        init(applicationContext.getBean(RyeCatcherProperties.class));
    }
}
