package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class SessionFilter extends GenericFilterBean {

    private final SessionManager sessionManager = InstanceHolder.getInstance(SessionManager.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            //TODO Principal
        }
        sessionManager.findSessionTokenFromClient(ConfigHolder.getTokenName()).ifPresent(sessionManager::renewal);
        filterChain.doFilter(servletRequest, servletResponse);
        SessionContextHolder.clear();
    }
}
