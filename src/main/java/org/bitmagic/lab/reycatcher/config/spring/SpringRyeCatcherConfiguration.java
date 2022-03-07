package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.TokenGenService;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.impl.CompositeTokenGenService;
import org.bitmagic.lab.reycatcher.impl.CookieTokenGenService;
import org.bitmagic.lab.reycatcher.impl.JwtTokenGenService;
import org.bitmagic.lab.reycatcher.impl.MemorySessionRepository;
import org.bitmagic.lab.reycatcher.utils.SpringContextHolder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.List;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Configuration
public class SpringRyeCatcherConfiguration {

    @PostConstruct
    public void init(RyeCatcherProperties properties){
        ConfigHolder.delegate = properties.getGenTokenTypeInfo()::get;
        InstanceHolder.delegate = SpringContextHolder::getBean;
    }

    @Bean
    public SessionRepository sessionRepository(){
        return new MemorySessionRepository();
    }

    @Bean
    public TokenGenService jwtTokenGenService(){
        return new JwtTokenGenService();
    }

    @Bean
    public TokenGenService cookieTokenGenService(){
        return new CookieTokenGenService();
    }

    @Bean
    @Primary
    public TokenGenService tokenGenService(List<TokenGenService> tokenGenServices){
        return new CompositeTokenGenService(tokenGenServices);
    }

    @Bean
    public SessionManager sessionManager(SessionRepository sessionRepository, TokenGenService tokenGenService){
        return new ServletSessionManager(sessionRepository,tokenGenService);
    }

    @Bean
    public FilterRegistrationBean<Filter> registrationBean(){
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new SessionFilter());
        bean.addUrlPatterns("/**");
        return bean;
    }

}
