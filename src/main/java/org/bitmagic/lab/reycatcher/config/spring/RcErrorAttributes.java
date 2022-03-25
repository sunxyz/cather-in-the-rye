package org.bitmagic.lab.reycatcher.config.spring;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Objects;

/**
 * @author yangrd
 */
public class RcErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(webRequest, options);
        Object status = webRequest.getAttribute("javax.servlet.error.cover.status_code", RequestAttributes.SCOPE_REQUEST);
        if(Objects.nonNull(status)){
            map.put("status", status);
        }
        Object message = webRequest.getAttribute("message", RequestAttributes.SCOPE_REQUEST);
        map.put("message", message);
        return map;
    }
}
