package org.bitmagic.lab.reycatcher.config.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.support.SessionContextHolder;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Slf4j
@RequiredArgsConstructor
public class ContextHolderInitFilter extends GenericFilterBean {

    private final SessionManager sessionManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            //TODO Principal
        }
        sessionManager.findReqTokenInfoFromClient(ConfigHolder.getOutClientTokenName()).map(reqTokenInfo -> SessionToken.of(ConfigHolder.getGenTokenType(), reqTokenInfo.getValue())).ifPresent(sessionManager::renewal);
        filterChain.doFilter(servletRequest, servletResponse);
        SessionContextHolder.clear();
        RcRequestContextHolder.clear();
    }
}