package org.bitmagic.lab.reycatcher.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.support.RyeCatcherContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author yangrd
 */
@Slf4j
public class RyeCatcherExceptionCatchFilter  extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (BasicException e) {
            log.warn("err class: {}, msg: {}", e.getClass(), e.getMessage());
            HttpServletResponse response = RyeCatcherContextHolder.getContext().getResponse();
            response.setStatus(SC_UNAUTHORIZED);
            response.addHeader("Basic", "realm= \""+e.getRealm()+"\"");
            response.flushBuffer();
        }
    }
}
