package org.bitmagic.lab.reycatcher.oauth2.store;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.bitmagic.lab.reycatcher.oauth2.support.OAuth2ExceptionUtils.tryOauth2Exception;

/**
 * @author yangrd
 */
public class MemoryOAuth2TokenStore implements OAuth2TokenStore {

    public static final OAuth2TokenStore INSTANCE = new MemoryOAuth2TokenStore();

    private final Map<String, Oauth2Token> tokenMap = new ConcurrentHashMap<>(32);
    private final Map<String, String> refreshTokenMap = new ConcurrentHashMap<>(32);
    //  clear expired token
    {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            tokenMap.values().stream().filter(token -> token.getCreatedTime().plusSeconds(token.getExpiresIn()).isAfter(now)).forEach(token -> removeToken(token.getAccessToken()));
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void storeToken(String token, Oauth2Token tokenInfo) {
        tokenMap.put(token, tokenInfo);
        refreshTokenMap.put(tokenInfo.getRefreshToken(), token);
    }

    @Override
    public Oauth2Token getTokenInfo(String token) {
        Oauth2Token oauth2Token = tokenMap.get(token);
        tryOauth2Exception(oauth2Token.getCreatedTime().plusSeconds(oauth2Token.expiresIn).isAfter(LocalDateTime.now()), "token expired");
        return oauth2Token;
    }

    @Override
    public Oauth2Token getTokenInfoByRefreshToken(String refreshToken) {
        String token = refreshTokenMap.get(refreshToken);
        if (token != null) {
            return tokenMap.get(token);
        }
        return null;
    }

    @Override
    public void removeToken(String token) {
        Oauth2Token tokenInfo = tokenMap.remove(token);
        if (tokenInfo != null) {
            refreshTokenMap.remove(tokenInfo.getRefreshToken());
        }
    }
}
