package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.SessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.impl.BaseSessionManager;
import org.bitmagic.lab.reycatcher.ReqTokenInfo;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/05
 */
public class ServletSessionManager extends BaseSessionManager {
    public ServletSessionManager(SessionRepository repository, SessionTokenGenFactory tokenGenService) {
        super(repository, tokenGenService);
    }

    @Override
    public Optional<ReqTokenInfo> findReqTokenInfoFromClient(String tokenName) {
        HttpServletRequest request = RcRequestContextHolder.getContext().getRequest();
        Optional<String> tokenOptional =  Optional.ofNullable(request.getHeader(tokenName));
        String token = tokenOptional.orElseGet(() ->
             Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(tokenName)).map(Cookie::getValue).findFirst().orElse(request.getParameter(tokenName))
        );
        return TokenParseUtils.findReqTokenInfo(token);
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        Cookie cookie = new Cookie(tokenName, session.getSessionToken().getToken());
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
