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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Configuration
public class SpringRyeCatcherConfiguration {

    @PostConstruct
    public void init(RyeCatcherProperties properties){
        ConfigHolder.delegate = properties.getTokenType2Config()::get;
        ConfigHolder.getGenTokenTypeDelegate = ()->{
            //優先匹配最長路徑
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            List<String> mathPaths = properties.getPathPrefix2TokenType().values().stream().filter(path -> request.getRequestURI().indexOf(path) == 0).collect(Collectors.toList());
            return mathPaths.stream().max(Comparator.comparingInt(String::length)).map(properties.getPathPrefix2TokenType()::get).orElseThrow(RuntimeException::new);
        };
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