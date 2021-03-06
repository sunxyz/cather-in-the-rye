package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.impl.BaseSessionManager;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/05
 */
public class ServletSessionManager extends BaseSessionManager {
    public static final String COOKIE_PATH = "cookiePath";

    public ServletSessionManager(SessionRepository repository, SessionTokenGenFactory tokenGenService) {
        super(repository, tokenGenService);
    }

    @Override
    public Optional<ReqTokenInfo> findReqTokenInfoFromClient(String tokenName) {
        HttpServletRequest request = RcRequestContextHolder.getContext().getRequest();
        Optional<String> tokenOptional = Optional.ofNullable(request.getHeader(tokenName));
        String token = tokenOptional.orElseGet(() ->
                Objects.nonNull(request.getCookies()) ? Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(tokenName)).map(Cookie::getValue).findFirst().orElse(request.getParameter(tokenName)) : request.getParameter(tokenName)
        );
        return TokenParseUtils.parseReqTokenInfo(token);
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        Cookie cookie = new Cookie(tokenName, SessionToken.GenTypeCons.JWT.equals(session.getSessionToken().getGenType()) ? "Bearer%20" + session.getSessionToken().getToken() : session.getSessionToken().getToken());
        if (DynamicRcConfigHolder.listCertificationSystemPredicate().isEmpty()) {
            cookie.setPath("/");
        } else if (Objects.nonNull(RcRequestContextHolder.getContext().getRequest().getAttribute(COOKIE_PATH))) {
            cookie.setPath(RcRequestContextHolder.getContext().getRequest().getAttribute(COOKIE_PATH).toString());
        }
        if (session.getMaxInactiveInterval() == 0) {
            cookie.setMaxAge(session.getMaxInactiveInterval());
        } else {
            cookie.setMaxAge(-1);
        }
        RcRequestContextHolder.getContext().getResponse().addCookie(cookie);
    }

    @Override
    public void renewal(SessionToken token) {
        super.renewal(token);
    }
}
