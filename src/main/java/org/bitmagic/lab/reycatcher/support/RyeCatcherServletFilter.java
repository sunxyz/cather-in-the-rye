package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesCreate;
import org.bitmagic.lab.reycatcher.reqmatches.ReqMatchesHandler;
import org.springframework.core.annotation.Order;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;

/**
 * @author yangrd
 */
@Order(-200)
public class RyeCatcherServletFilter extends HttpFilter {

    private  ReqMatchesHandler reqMatchesHandler;

    public RyeCatcherServletFilter(Function<ReqMatchesCreate, ReqMatchesHandler> reqMatchesFactory) {
        this.reqMatchesHandler = reqMatchesFactory.apply(ReqMatchesCreate.INSTANCE);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (reqMatchesHandler.handler(request)){
           chain.doFilter(request,response);
        }else {
            response.getWriter().print("not pass");
        }
    }

    @Override
    public void destroy() {
        reqMatchesHandler=null;
    }
}
