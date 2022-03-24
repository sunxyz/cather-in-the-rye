package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.ex.ForbiddenException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yangrd
 */
@ControllerAdvice
public class RcWebExceptionHandler {

    @ExceptionHandler(RyeCatcherException.class)
    public String handlerRc(RyeCatcherException e, HttpServletRequest request) {
        request.setAttribute("javax.servlet.error.status_code", e instanceof ForbiddenException ? 403 : 401);
        request.setAttribute("message", e.getMessage());
        // do something
        return "forward:/error";
    }
}
