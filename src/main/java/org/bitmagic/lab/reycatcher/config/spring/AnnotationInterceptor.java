package org.bitmagic.lab.reycatcher.config.spring;


import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.CheckPermissions;
import org.bitmagic.lab.reycatcher.CheckRoles;
import org.bitmagic.lab.reycatcher.MatchRelation;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * @author bloom
 */
@RequiredArgsConstructor
public class AnnotationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handler1 = (HandlerMethod) handler;
            CheckPermissions checkPermissions = getAnnotation(handler1, CheckPermissions.class);
            if (Objects.nonNull(checkPermissions)) {
                RyeCatcher.check("perm", checkPermissions.matchRelation(), checkPermissions.value());
            }
            CheckRoles checkRoles = getAnnotation(handler1, CheckRoles.class);
            if (Objects.nonNull(checkRoles)) {
                RyeCatcher.check("role", checkRoles.matchRelation(), checkRoles.value());
            }
            //jsr-250
            RolesAllowed rolesAllowed = getAnnotation(handler1, RolesAllowed.class);
            if (Objects.nonNull(rolesAllowed)) {
                RyeCatcher.check("role", MatchRelation.ALL, rolesAllowed.value());
            }
            PermitAll permitAll = getAnnotation(handler1, PermitAll.class);
            if (Objects.nonNull(permitAll)) {
                ValidateUtils.checkAuthority(RyeCatcher.isLogin(),"please login!");
            }
            ValidateUtils.checkAuthority(Objects.isNull(getAnnotation(handler1, DenyAll.class)),"denyAll!");
            return true;
        } else {
            return true;
        }
    }

    private <T extends Annotation> T getAnnotation(HandlerMethod handler, Class<T> tClass) {
        T annotation = handler.getMethod().getAnnotation(tClass);
        return Objects.isNull(annotation)?handler.getBeanType().getAnnotation(tClass):annotation;
    }
}
