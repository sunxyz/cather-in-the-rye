package org.bitmagic.lab.reycatcher.filter;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.ex.ForbiddenException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.bitmagic.lab.reycatcher.reqmatches.BiConsumers;
import org.bitmagic.lab.reycatcher.utils.Base64Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author bloom
 */
@Slf4j
public  class RyeCatcherErrHandlerServletFilter<T extends RyeCatcherErrHandlerServletFilter<T>> extends HttpFilter{

    private BiConsumer<RuntimeException, HttpServletResponse> errCatcher;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException(request + " not HttpServletRequest");
        } else if (!(response instanceof HttpServletResponse)) {
            throw new ServletException(request + " not HttpServletResponse");
        } else {
            HttpServletResponse response1 = (HttpServletResponse) response;
            try {
                this.doFilter((HttpServletRequest)request, response1, chain);
            } catch (RyeCatcherException e) {
                if (existErrCatcher()) {
                    errCatcher.accept(e, response1);
                } else {
                    log.warn("error class: {} msg:{}", e.getClass(), e.getMessage());
                    if(e instanceof BasicException){
                        response1.setStatus(SC_UNAUTHORIZED);
                        response1.setHeader("WWW-Authenticate", "Basic realm=" + Base64Utils.encode(((BasicException)e).getRealm()));
                    }else {
                        response1.sendError(e instanceof ForbiddenException ? 403 : 401, e.getMessage());
                    }
                }
            }
        }
    }

    public T setErrCatcher(BiConsumer<RuntimeException, HttpServletResponse> errCatcher) {
        this.errCatcher = errCatcher;
        return (T)this;
    }

    public T setErrCatcher(Consumer<RuntimeException> errCatcher) {
        return setErrCatcher(BiConsumers.of(errCatcher));
    }

    public T setErrCatcher(Function<RuntimeException, Object> errCatcher) {
        return setErrCatcher((e, response) -> {
            try {
                Object apply = errCatcher.apply(e);
                if(Objects.isNull(response.getContentType())){
                    response.setContentType("application/json;charset=UTF-8");
                    if(apply instanceof String){
                        response.setContentType("text/plain;charset=UTF-8");
                    }
                }
                response.getWriter().print(apply);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private boolean existErrCatcher() {
        return Objects.nonNull(errCatcher);
    }

    @Override
    public void destroy() {
        errCatcher= null;
    }
}
