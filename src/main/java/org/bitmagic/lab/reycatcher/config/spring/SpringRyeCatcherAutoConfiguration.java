package org.bitmagic.lab.reycatcher.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.CertificationSystemDefine;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.impl.CompositeSessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.JwtSessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.MemorySessionRepository;
import org.bitmagic.lab.reycatcher.impl.SessionIdSessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.predicates.CertificationSystemPredicate;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.RyeCatcherServletFilter;
import org.bitmagic.lab.reycatcher.utils.SpringContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Configuration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties(RyeCatcherProperties.class)
@Import(CsPredicateConfiguration.class)
@Slf4j
public class SpringRyeCatcherAutoConfiguration implements ApplicationContextAware {

    private static final CertificationSystemDefine DEFAULT_CERTIFICATION_SYSTEM_INFO = CertificationSystemDefine.of("default-id", Collections.emptyList(), SessionToken.GenTypeCons.SESSION_ID, null,false, "JSESSIONID", 30 * 60 * 1000, true, true, true);

    public void init(RyeCatcherProperties properties, CertificationSystemPredicate certificationSystemPredicate) {
        DynamicRcConfigHolder.delegate = () -> {
            if (Objects.isNull(properties.getCertificationSystems())) {
                return DEFAULT_CERTIFICATION_SYSTEM_INFO;
            }
            HttpServletRequest request = RcRequestContextHolder.getContext().getRequest();
            List<CertificationSystemDefine> systemDefines = properties.getCertificationSystems().stream().filter(o -> mathCertificationSystemDefine(o,certificationSystemPredicate,request)).collect(Collectors.toList());
            if (systemDefines.size() != 1) {
                throw new IllegalStateException("systemDefines == 1");
            }
            return systemDefines.iterator().next();
        };
        InstanceHolder.delegate = c -> {
            try {
                return SpringContextHolder.getBean(c);
            } catch (BeansException beansException) {
                log.warn("BeansException msg:{}", beansException.getMessage());
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

    @Bean
    public FilterRegistrationBean<Filter> registrationSessionFilter(SessionManager sessionManager) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new ContextHolderInitFilter(sessionManager));
        bean.addUrlPatterns("/*");
        bean.setName("registrationSessionFilter");
        bean.setOrder(10);
        return bean;
    }


    @ConditionalOnBean(RyeCatcherServletFilter.class)
    @Bean
    public FilterRegistrationBean<Filter> registrationRyeCatcherServletFilter(RyeCatcherServletFilter ryeCatcherServletFilter) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(ryeCatcherServletFilter);
        bean.addUrlPatterns("/*");
        bean.setName("RyeCatcherServletFilter");
        bean.setOrder(100);
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
        init(applicationContext.getBean(RyeCatcherProperties.class), applicationContext.getBean(CertificationSystemPredicate.class));
    }

    private boolean mathCertificationSystemDefine(CertificationSystemDefine certificationSystemDefine, CertificationSystemPredicate certificationSystemPredicate, HttpServletRequest request){
        return certificationSystemDefine.getPredicates().stream().allMatch(t -> {
//                    Path=k:v,k1:v1
            String[] split = t.split("=");
            String name = split[0];
            String values = split[1];
            String[] kvs = null;
            if (values.contains(",")) {
                kvs = values.split(",");
            } else {
                kvs = new String[]{values};
            }
            Map<String, String> map = new HashMap<>();
            for (String kv : kvs) {
                if (kv.contains(":")) {
                    String[] kv0 = kv.split(":");
                    map.put(kv0[0], kv0[1]);
                } else {
                    map.put(kv, "true");
                }
            }
            return certificationSystemPredicate.test(name, request, map);
        });
    }
}
