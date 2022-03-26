package org.bitmagic.lab.reycatcher.config.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.SessionContextHolder;
import org.bitmagic.lab.reycatcher.support.ThreadLocalRcRequestContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@Slf4j
@RequiredArgsConstructor
public class ContextHolderInitFilter extends GenericFilterBean {

    private final SessionManager sessionManager;
    private boolean noInitOnceFlag = true;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            //TODO Principal
        }
        if(noInitOnceFlag){
            init();
        }
        sessionManager.findReqTokenInfoFromClient(DynamicRcConfigHolder.getOutClientTokenName()).map(reqTokenInfo -> SessionToken.of(DynamicRcConfigHolder.getGenTokenType(), reqTokenInfo.getValue())).ifPresent(sessionManager::renewal);
        filterChain.doFilter(servletRequest, servletResponse);
        SessionContextHolder.clear();
        RcRequestContextHolder.clear();
    }

    private void init(){
        RcRequestContextHolder.setContext(ThreadLocalRcRequestContext.of(((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))::getRequest,((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())::getResponse));
        noInitOnceFlag = false;
    }
}
