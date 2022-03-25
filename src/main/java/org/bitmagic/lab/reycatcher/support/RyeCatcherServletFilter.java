package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.ex.ForbiddenException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesCreate;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesHandler;
import org.bitmagic.lab.reycatcher.utils.Base64Utils;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author yangrd
 */
@Order(-200)
public class RyeCatcherServletFilter extends HttpFilter {

    private ReqMatchesHandler reqMatchesHandler;

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
            response.sendError(SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=" + Base64Utils.encode(ex.getRealm()));
            response.flushBuffer();
        } catch (RyeCatcherException e) {
            request.setAttribute("javax.servlet.error.cover.status_code", e instanceof ForbiddenException ? 403 : 401);
            request.setAttribute("message", e.getMessage());
            throw e;
        }
    }

    @Override
    public void destroy() {
        reqMatchesHandler = null;
    }
}
