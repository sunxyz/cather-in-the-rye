package org.bitmagic.lab.reycatcher.support;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.filter.RyeCatcherErrHandlerServletFilter;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatcher;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;

/**
 * @author yangrd
 */
@Slf4j
public class RyeCatcherServletFilter extends RyeCatcherErrHandlerServletFilter<RyeCatcherServletFilter> {

    private ReqMatchesHandler reqMatchesHandler;

    public RyeCatcherServletFilter(Function<ReqMatcher, ReqMatchesHandler> reqMatchesFactory) {
        this.reqMatchesHandler = reqMatchesFactory.apply(ReqMatcher.INSTANCE);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (reqMatchesHandler.handler(request)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        reqMatchesHandler = null;
    }

}
