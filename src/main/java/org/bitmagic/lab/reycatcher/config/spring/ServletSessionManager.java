package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.TokenGenFactory;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.impl.BaseSessionManager;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;
import org.bitmagic.lab.reycatcher.utils.StringUtils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/05
 */
public class ServletSessionManager extends BaseSessionManager {
    public ServletSessionManager(SessionRepository repository, TokenGenFactory tokenGenService) {
        super(repository, tokenGenService);
    }

    @Override
    public Optional<SessionToken> findSessionTokenFromClient(String tokenName) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Optional<String> tokenOptional = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(tokenName)).map(Cookie::getValue).findFirst();
        String token = tokenOptional.orElseGet(() -> {
            String v = request.getHeader(tokenName);
            return Objects.isNull(v) ? request.getParameter(tokenName) : v;
        });
        return TokenParseUtils.getSessionToken(token);
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        ValidateUtils.notNull(response, "response not null");
        Cookie cookie = new Cookie(tokenName, session.getSessionToken().getToken());
        if (session.getMaxInactiveInterval() == 0) {
            cookie.setMaxAge(session.getMaxInactiveInterval());
        } else {
            cookie.setMaxAge(-1);
        }
        response.addCookie(cookie);
    }

    @Override
    public void renewal(SessionToken token) {
        super.renewal(token);
    }
}
