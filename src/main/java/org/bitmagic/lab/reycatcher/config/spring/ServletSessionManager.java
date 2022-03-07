package org.bitmagic.lab.reycatcher.config.spring;

import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.TokenGenService;
import org.bitmagic.lab.reycatcher.impl.BaseSessionManager;
import org.bitmagic.lab.reycatcher.utils.StringUtils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangrd
 */
public class ServletSessionManager extends BaseSessionManager {
    public ServletSessionManager(SessionRepository repository, TokenGenService tokenGenService) {
        super(repository, tokenGenService);
    }

    @Override
    public Optional<SessionToken> findSessionTokenFromClient(String tokenName) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Optional<String> tokenOptional = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(tokenName)).map(Cookie::getValue).findFirst();
        String token = tokenOptional.orElseGet(()->{
            String v = request.getHeader(tokenName);
            return Objects.isNull(v)?request.getParameter(tokenName):v;
        });
        return StringUtils.isEmpty(token)?Optional.of(SessionToken.of(token.contains(".")?SessionToken.TokenTypeCons.JWT_TOKEN:SessionToken.TokenTypeCons.COOKIE,token)):Optional.empty();
    }

    @Override
    public void outSession2Client(String tokenName, Session session) {
        if(session.getMeta() instanceof Map){
            ((Map) session.getMeta()).put("tokenName", tokenName);
        }
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        ValidateUtils.notNull(response,"response not null");
        Cookie cookie = new Cookie(tokenName, session.getSessionToken().getToken());
        cookie.setMaxAge(Long.valueOf((session.getTimeOutMillisecond()-System.currentTimeMillis())/1000).intValue());
        response.addCookie(cookie);
    }

    @Override
    public void renewal(SessionToken token) {
        findByToken(token).ifPresent(session -> {
            if(session.isNeedOutClient()&&session.getMeta() instanceof Map){
                outSession2Client(((Map)session.getMeta()).get("tokenName").toString(), session);
            }
            super.renewal(token);
        });
    }
}
