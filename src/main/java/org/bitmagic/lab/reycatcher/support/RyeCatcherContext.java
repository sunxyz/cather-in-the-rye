package org.bitmagic.lab.reycatcher.support;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yangrd
 */
public interface RyeCatcherContext {

    default HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }

    default HttpServletResponse getResponse(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }
}
