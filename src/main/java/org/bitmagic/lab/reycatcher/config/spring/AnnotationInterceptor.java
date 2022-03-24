package org.bitmagic.lab.reycatcher.config.spring;


import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.helper.RcBasicHelper;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author yangrd
 * @date 2022/03/05
 */
@RequiredArgsConstructor
public class AnnotationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handler1 = (HandlerMethod) handler;
            CheckPermission checkPermission = getAnnotation(handler1, CheckPermission.class);
            if (Objects.nonNull(checkPermission)) {
                CheckRole or = checkPermission.or();
                boolean flag = RyeCatcher.has("perm", checkPermission.matchRelation(), checkPermission.value())||(or.value().length != 0 && RyeCatcher.has("role", or.matchRelation(), or.value()));
                ValidateUtils.checkGrant(flag, checkPermission.matchRelation()+": "+String.join(",", checkPermission.value())+" or "+or.matchRelation()+": " + String.join(",", or.value()));
            }
            CheckRole checkRole = getAnnotation(handler1, CheckRole.class);
            if (Objects.nonNull(checkRole)) {
                RyeCatcher.check("role", checkRole.matchRelation(), checkRole.value());
            }
            //jsr-250
            RolesAllowed rolesAllowed = getAnnotation(handler1, RolesAllowed.class);
            if (Objects.nonNull(rolesAllowed)) {
                RyeCatcher.check("role", MatchRelation.ALL, rolesAllowed.value());
            }
            PermitAll permitAll = getAnnotation(handler1, PermitAll.class);
            if (Objects.nonNull(permitAll)) {
                RyeCatcher.checkLogin();
            }
            CheckBasic checkBasic = getAnnotation(handler1, CheckBasic.class);
            if (Objects.nonNull(checkBasic)) {
                RcBasicHelper.check(checkBasic.value(), checkBasic.realm());
            }
            ValidateUtils.checkGrant(Objects.isNull(getAnnotation(handler1, DenyAll.class)), "denyAll!");
            return true;
        } else {
            return true;
        }
    }

    private <T extends Annotation> T getAnnotation(HandlerMethod handler, Class<T> tClass) {
        T annotation = handler.getMethod().getAnnotation(tClass);
        return Objects.isNull(annotation) ? handler.getBeanType().getAnnotation(tClass) : annotation;
    }
}
