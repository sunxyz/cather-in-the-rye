package org.bitmagic.lab.reycatcher.support;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.ex.ForbiddenException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.bitmagic.lab.reycatcher.reqmatches.BiConsumers;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesCreate;
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

    public RyeCatcherServletFilter(Function<ReqMatchesCreate, ReqMatchesHandler> reqMatchesFactory) {
        this.reqMatchesHandler = reqMatchesFactory.apply(ReqMatchesCreate.INSTANCE);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (reqMatchesHandler.handler(request)) {
                chain.doFilter(request, response);
            }
        } catch (BasicException ex) {
            if (existErrCatcher()) {
                errCatcher.accept(ex, response);
            } else {
                log.warn("error class: {} msg:{}", ex.getClass(), ex.getMessage());
                response.setStatus(SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "Basic realm=" + Base64Utils.encode(ex.getRealm()));
            }
        } catch (RyeCatcherException e) {
            if (existErrCatcher()) {
                errCatcher.accept(e, response);
            } else {
                log.warn("error class: {} msg:{}", e.getClass(), e.getMessage());
                response.sendError(e instanceof ForbiddenException ? 403 : 401, e.getMessage());
            }
        }
    }

    public void setErrCatcher(BiConsumer<RuntimeException, HttpServletResponse> errCatcher) {
        this.errCatcher = errCatcher;
    }

    public void setErrCatcher(Consumer<RuntimeException> errCatcher) {
        this.errCatcher = BiConsumers.of(errCatcher);
    }

    public void setErrCatcher(Function<RuntimeException, Object> errCatcher) {
        this.errCatcher = ((e, response) -> {
            try {
                response.setHeader("Content-Type", "application/json");
                response.getWriter().print(errCatcher.apply(e));
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
