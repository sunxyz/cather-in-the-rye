package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.impl.CompositeTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.CookieTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.JwtTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.MemorySessionRepository;
import org.bitmagic.lab.reycatcher.utils.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
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
public class SpringRyeCatcherConfiguration {

    private static final RyeCatcherProperties.CertificationSystemInfo DEFAULT_CERTIFICATION_SYSTEM_INFO = RyeCatcherProperties.CertificationSystemInfo.of(SessionToken.TokenTypeCons.COOKIE, "JSESSIONID", 30 * 60 * 1000, true, true, true,"/");

    @PostConstruct
    public void init(RyeCatcherProperties properties) {
        ConfigHolder.delegate = () -> {
            if(Objects.isNull(properties.getMultiCertificationSystemInfo())){
                return DEFAULT_CERTIFICATION_SYSTEM_INFO;
            }
            //優先匹配最長路徑
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            List<String> mathPaths = properties.getMultiCertificationSystemInfo().keySet().stream().filter(path -> request.getRequestURI().indexOf(path) == 0).collect(Collectors.toList());

            return mathPaths.isEmpty() ? DEFAULT_CERTIFICATION_SYSTEM_INFO : mathPaths.stream().max(Comparator.comparingInt(String::length)).map(path->{
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
    public TokenGenFactory jwtTokenGenService() {
        return new JwtTokenGenFactory();
    }

    @Bean
    public TokenGenFactory cookieTokenGenService() {
        return new CookieTokenGenFactory();
    }

    @Bean
    @Primary
    public TokenGenFactory tokenGenService(List<TokenGenFactory> tokenGenServices) {
        return new CompositeTokenGenFactory(tokenGenServices);
    }

    @Bean
    public SessionManager sessionManager(SessionRepository sessionRepository, TokenGenFactory tokenGenService) {
        return new ServletSessionManager(sessionRepository, tokenGenService);
    }

    @ConditionalOnMissingBean(RyeCatcherListener.class)
    @Bean
    public RyeCatcherListener ryeCatcherListener(){
        return new DefaultRyeCatcherListener();
    }

    @Bean
    public FilterRegistrationBean<Filter> registrationSessionFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new SessionFilter());
        bean.addUrlPatterns("/**");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<Filter> registrationExceptionCatchFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new RyeCatcherExceptionCatchFilter());
        bean.addUrlPatterns("/**");
        return bean;
    }

}
