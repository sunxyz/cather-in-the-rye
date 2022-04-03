package org.bitmagic.lab.reycatcher.support;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.ex.ForbiddenException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.bitmagic.lab.reycatcher.reqmatches.BiConsumers;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatcher;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesHandler;
import org.bitmagic.lab.reycatcher.utils.Base64Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
 * @author yangrd
 */
@Slf4j
public class RyeCatcherServletFilter extends HttpFilter {

    private ReqMatchesHandler reqMatchesHandler;

    private BiConsumer<RuntimeException, HttpServletResponse> errCatcher;

    public RyeCatcherServletFilter(Function<ReqMatcher, ReqMatchesHandler> reqMatchesFactory) {
        this.reqMatchesHandler = reqMatchesFactory.apply(ReqMatcher.INSTANCE);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (reqMatchesHandler.handler(request)) {
                chain.doFilter(request, response);
            }
        } catch (RyeCatcherException e) {
            if (existErrCatcher()) {
                errCatcher.accept(e, response);
            } else {
                log.warn("error class: {} msg:{}", e.getClass(), e.getMessage());
                if(e instanceof BasicException){
                    response.setStatus(SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "Basic realm=" + Base64Utils.encode(((BasicException)e).getRealm()));
                }else {
                    response.sendError(e instanceof ForbiddenException ? 403 : 401, e.getMessage());
                }
            }
        }
    }

    public RyeCatcherServletFilter setErrCatcher(BiConsumer<RuntimeException, HttpServletResponse> errCatcher) {
        this.errCatcher = errCatcher;
        return this;
    }

    public RyeCatcherServletFilter setErrCatcher(Consumer<RuntimeException> errCatcher) {
        return setErrCatcher(BiConsumers.of(errCatcher));
    }

    public RyeCatcherServletFilter setErrCatcher(Function<RuntimeException, Object> errCatcher) {
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

    @Override
    public void destroy() {
        reqMatchesHandler = null;
    }

    private boolean existErrCatcher() {
        return Objects.nonNull(errCatcher);
    }
}
