package org.bitmagic.lab.reycatcher.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.ex.ForbiddenException;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author yangrd
 */
@ControllerAdvice
@Slf4j
public class RcWebExceptionHandler {

    @ExceptionHandler(RyeCatcherException.class)
    public String handlerRc(RyeCatcherException e, HttpServletRequest request) {
        log.warn("err class: {}, msg: {}", e.getClass(), e.getMessage());

        request.setAttribute("javax.servlet.error.status_code", e instanceof ForbiddenException ? 403 : 401);
        request.setAttribute("message", e.getMessage());
        // do something
        return "forward:/error";
    }

    @ExceptionHandler(BasicException.class)
    public ResponseEntity<Object> t(BasicException e){
        return ResponseEntity.status(SC_UNAUTHORIZED).header("WWW-Authenticate","Basic realm=\""+e.getRealm()+"\"").build();
    }
}
