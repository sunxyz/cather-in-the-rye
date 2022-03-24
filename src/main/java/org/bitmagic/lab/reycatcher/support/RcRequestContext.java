package org.bitmagic.lab.reycatcher.support;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yangrd
 */
public interface RcRequestContext {

    default HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }

    default HttpServletResponse getResponse(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }

    void setAttr(String key, Object v);

    <T>T getAttr(String key, Class<T> tClass);

    Object getAttr(String key);

    boolean containsAttr(String key);

    void removeAttr(String key);

    void clearAll();
}
