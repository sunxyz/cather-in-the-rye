package org.bitmagic.lab.reycatcher.config.spring;

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
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
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
public class SpringRyeCatcherConfiguration implements ApplicationContextAware {

    private static final RyeCatcherProperties.CertificationSystemInfo DEFAULT_CERTIFICATION_SYSTEM_INFO = RyeCatcherProperties.CertificationSystemInfo.of(SessionToken.GenTypeCons.SESSION_ID, "JSESSIONID", 30 * 60 * 1000, true, true, true, "/", "");

    public void init(RyeCatcherProperties properties) {
        ConfigHolder.delegate = () -> {
            if (Objects.isNull(properties.getMultiCertificationSystemInfo())) {
                return DEFAULT_CERTIFICATION_SYSTEM_INFO;
            }
            //優先匹配最長路徑
            HttpServletRequest request = RcRequestContextHolder.getContext().getRequest();
            List<String> mathPaths = properties.getMultiCertificationSystemInfo().keySet().stream().filter(path -> request.getRequestURI().indexOf(path) == 0).collect(Collectors.toList());

            return mathPaths.isEmpty() ? DEFAULT_CERTIFICATION_SYSTEM_INFO : mathPaths.stream().max(Comparator.comparingInt(String::length)).map(path -> {
                RyeCatcherProperties.CertificationSystemInfo certificationSystemInfo = properties.getMultiCertificationSystemInfo().get(path);
                certificationSystemInfo.setRyeCatcherPath(path);
                return certificationSystemInfo;
            }).orElseThrow(RuntimeException::new);
        };
        InstanceHolder.delegate = SpringContextHolder::getBean;
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
    public RcErrorAttributes rcErrorAttributes(){
        return new RcErrorAttributes();
    }

    @Bean
    public RcWebExceptionHandler rcWebExceptionHandler(){return new RcWebExceptionHandler();}

    @Bean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        init( applicationContext.getBean(RyeCatcherProperties.class));
    }
}
