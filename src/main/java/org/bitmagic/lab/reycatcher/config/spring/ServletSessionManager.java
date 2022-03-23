package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.SessionTokenGenFactory;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.impl.BaseSessionManager;
import org.bitmagic.lab.reycatcher.support.RyeCatcherContextHolder;
import org.bitmagic.lab.reycatcher.utils.StringUtils;

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
    public ServletSessionManager(SessionRepository repository, SessionTokenGenFactory tokenGenService) {
        super(repository, tokenGenService);
    }

    @Override
    public Optional<SessionToken> findSessionTokenFromClient(String tokenName) {
        HttpServletRequest request = RyeCatcherContextHolder.getContext().getRequest();
        Optional<String> tokenOptional = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(tokenName)).map(Cookie::getValue).findFirst();
        String token = tokenOptional.orElseGet(() -> {
            String v = request.getHeader(tokenName);
            return Objects.isNull(v) ? request.getParameter(tokenName) : v;
        });
        return StringUtils.isEmpty(token)?Optional.empty():Optional.of(SessionToken.of(ConfigHolder.getGenTokenType(), token));
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        Cookie cookie = new Cookie(tokenName, session.getSessionToken().getToken());
        if (session.getMaxInactiveInterval() == 0) {
            cookie.setMaxAge(session.getMaxInactiveInterval());
        } else {
            cookie.setMaxAge(-1);
        }
        RyeCatcherContextHolder.getContext().getResponse().addCookie(cookie);
    }

    @Override
    public void renewal(SessionToken token) {
        super.renewal(token);
    }
}
