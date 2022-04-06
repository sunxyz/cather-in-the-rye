package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.oauth2.config.OAuth2AuthorizationServerClientsConfigurer;
import org.bitmagic.lab.reycatcher.oauth2.model.*;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2Approval;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2ApprovalStore;
import org.bitmagic.lab.reycatcher.oauth2.store.OAuth2TokenStore;
import org.bitmagic.lab.reycatcher.oauth2.store.Oauth2Token;
import org.bitmagic.lab.reycatcher.oauth2.support.OAuth2Exception;
import org.bitmagic.lab.reycatcher.support.SessionContextHolder;
import org.bitmagic.lab.reycatcher.utils.IdGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.bitmagic.lab.reycatcher.oauth2.support.OAuth2ExceptionUtils.tryOauth2Exception;

/**
 * @author yangrd
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthorizationServer {

    private static final Map<String, CodeInfo> CODE_INFO_REPO = new ConcurrentHashMap<>();
    private final OAuth2AuthorizationServerClientsConfigurer oAuth2ServerClientsConfigurer;
    private final OAuth2TokenStore tokenStore;
    private final OAuth2ApprovalStore approvalStore;
    private final String loginPath;
    private final String confirmPath;

    {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            CODE_INFO_REPO.entrySet().removeIf(entry -> entry.getValue().expireTime.isBefore(now));
        }, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 授权码模式 授权 访问 -> code
     *
     * @param authorizeInfo
     * @param response
     */
    public void authorize(AuthorizeInfo authorizeInfo, HttpServletResponse response) throws IOException {
        // 有无登录
        if (!RyeCatcher.isLogin()) {
            response.sendRedirect(loginPath + String.format("?clientId=%s&redirectUri=%s&responseType=%s&scope=%s&state=%s", authorizeInfo.getClientId(), authorizeInfo.getRedirectUri(), authorizeInfo.getResponseType(), authorizeInfo.getScope(), authorizeInfo.getState()));
        } else {
            OAuth2AuthorizationServerClientInfo oauth2AuthorizationServerClientInfo = oAuth2ServerClientsConfigurer.getClientInfo(authorizeInfo.getClientId());
            try {
                authorize0(authorizeInfo, response, oauth2AuthorizationServerClientInfo);
            } catch (OAuth2Exception e) {
                log.warn("authorize error", e);
                response.sendRedirect(authorizeInfo.getRedirectUri() + "?error=" + e.getMessage() + "&state=" + authorizeInfo.getState());
            }
        }
    }


    // 登录 不做

    public void confirmAccess(ConfirmAccessInfo confirmAccessInfo, HttpServletResponse response) throws IOException {
        AuthorizeInfo authorizeInfo = (AuthorizeInfo) getSession().getAttribute("authorizeInfo");
        OAuth2AuthorizationServerClientInfo oauth2AuthorizationServerClientInfo = oAuth2ServerClientsConfigurer.getClientInfo(authorizeInfo.getClientId());
        try {
            tryOauth2Exception(!confirmAccessInfo.isApproval(), "invalid_approval");
            OAuth2Approval auth2Approval = OAuth2Approval.of(RyeCatcher.getLoginId(), authorizeInfo.getClientId(), authorizeInfo.getScope(), null, LocalDateTime.now().plusYears(1), LocalDateTime.now(), LocalDateTime.now());
            approvalStore.addApproval(auth2Approval);
            authorize0(authorizeInfo, response, oauth2AuthorizationServerClientInfo);
        } catch (OAuth2Exception e) {
            log.warn("authorize error", e);
            response.sendRedirect(String.format("%s?error=%s&state=%s", authorizeInfo.getRedirectUri(), e.getMessage(), authorizeInfo.getState()));
        }

    }

    /**
     * code -> 换取 access_token
     *
     * @param requestTokenInfo
     * @return
     */
    public Oauth2Token getOauth2Token(RequestTokenInfo requestTokenInfo) {
        // check client_id,  redirect_uri, code, redirectUri

        OAuth2AuthorizationServerClientInfo oauth2AuthorizationServerClientInfo = oAuth2ServerClientsConfigurer.getClientInfo(requestTokenInfo.getClientId());
        tryOauth2Exception(Objects.isNull(oauth2AuthorizationServerClientInfo), "invalid_client");
        tryOauth2Exception(oauth2AuthorizationServerClientInfo.getClientSecret() != null && !oauth2AuthorizationServerClientInfo.getClientSecret().equals(requestTokenInfo.getClientSecret()), "invalid_client_secret");
        tryOauth2Exception(!oauth2AuthorizationServerClientInfo.getGrantTypes().contains(requestTokenInfo.getGrantType()), "invalid_grant_type");
        tryOauth2Exception(Objects.nonNull(oauth2AuthorizationServerClientInfo.getRedirectUri()) && !(oauth2AuthorizationServerClientInfo.getRedirectUri().equals(requestTokenInfo.getRedirectUri())), "invalid_redirect_uri");
        if (requestTokenInfo.getGrantType().equals(GrantType.AUTHORIZATION_CODE)) {
            return authorizationCode(requestTokenInfo.getCode(), oauth2AuthorizationServerClientInfo);
        } else if (requestTokenInfo.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
            return refreshAccessToken(requestTokenInfo.getRefreshToken());
        } else if (requestTokenInfo.getGrantType().equals(GrantType.PASSWORD)) {
            throw new OAuth2Exception("not support password grant type");
        } else if (requestTokenInfo.getGrantType().equals(GrantType.CLIENT_CREDENTIALS)) {
            return clientCredentials(oauth2AuthorizationServerClientInfo);
        } else {
            throw new OAuth2Exception("invalid_grant_type");
        }
    }

    private Oauth2Token clientCredentials(OAuth2AuthorizationServerClientInfo serverClientInfo) {
        Oauth2Token tokenInfo = Oauth2Token.of(IdGenerator.genUuid(), IdGenerator.genUuid(), "bearer", serverClientInfo.getAccessTokenExpireTime(), String.join(",", serverClientInfo.getScopes()), serverClientInfo.getClientId(), serverClientInfo.getResourceIds(), serverClientInfo.getRefreshTokenExpireTime());
        tokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }

    private Oauth2Token authorizationCode(String code, OAuth2AuthorizationServerClientInfo serverClientInfo) {
        CodeInfo codeInfo = CODE_INFO_REPO.get(code);
        tryOauth2Exception(Objects.isNull(codeInfo), "invalid_code");
        Oauth2Token tokenInfo = Oauth2Token.of(IdGenerator.genUuid(), IdGenerator.genUuid(), "bearer", serverClientInfo.getAccessTokenExpireTime(), codeInfo.getScope(), codeInfo.getUserId(), serverClientInfo.getResourceIds(), serverClientInfo.getRefreshTokenExpireTime());
        tokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }

    /**
     * refresh_token -> 换取 access_token
     *
     * @param refreshToken
     * @return
     */
    private Oauth2Token refreshAccessToken(String refreshToken) {
        Oauth2Token oldTokenInfo = tokenStore.getTokenInfoByRefreshToken(refreshToken);
        tryOauth2Exception(Objects.isNull(oldTokenInfo), "invalid_refresh_token");
        tokenStore.removeToken(oldTokenInfo.getAccessToken());
        Oauth2Token tokenInfo = oldTokenInfo.clone(IdGenerator.genUuid());
        tokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }

    /**
     * checkToken
     *
     * @param accessToken
     */
    public void checkToken(String accessToken) {
        tryOauth2Exception(Objects.isNull(tokenStore.getTokenInfo(accessToken)), "invalid_access_token");
    }

    /**
     * revokeToken
     *
     * @param accessToken
     */
    public void revokeToken(String accessToken) {
        tokenStore.removeToken(accessToken);
    }

    private void authorize0(AuthorizeInfo authorizeInfo, HttpServletResponse response, OAuth2AuthorizationServerClientInfo oauth2AuthorizationServerClientInfo) throws IOException {
        // check client_id,  redirect_uri, response_type, scope
        tryOauth2Exception(Objects.isNull(oauth2AuthorizationServerClientInfo), "invalid_client");
        if (approvalStore.containsApproval(authorizeInfo.getClientId(), RyeCatcher.getLoginId(), authorizeInfo.getScope())) {
            tryOauth2Exception(!oauth2AuthorizationServerClientInfo.getScopes().contains(authorizeInfo.getScope()), "invalid_scope");
            tryOauth2Exception(Objects.nonNull(oauth2AuthorizationServerClientInfo.getRedirectUri()) && !oauth2AuthorizationServerClientInfo.getRedirectUri().equals(authorizeInfo.getRedirectUri()), "invalid_redirect_uri");
            if (authorizeInfo.getResponseType().contains(ResponseType.CODE)) {
                tryOauth2Exception(!oauth2AuthorizationServerClientInfo.getGrantTypes().contains(GrantType.AUTHORIZATION_CODE), "unsupported_grant_type");
                String userId = RyeCatcher.getLoginId();
                String code = IdGenerator.genUuid();
                CODE_INFO_REPO.put(code, CodeInfo.of(code, userId, oauth2AuthorizationServerClientInfo.getClientId(), oauth2AuthorizationServerClientInfo.getRedirectUri(), authorizeInfo.getScope()));
                response.sendRedirect(authorizeInfo.getRedirectUri() + "?code=" + code + "&state=" + authorizeInfo.getState());
            } else if (authorizeInfo.getResponseType().contains(ResponseType.TOKEN)) {
                tryOauth2Exception(!oauth2AuthorizationServerClientInfo.getGrantTypes().contains(GrantType.IMPLICIT), "unsupported_grant_type");
                Oauth2Token tokenInfo = Oauth2Token.of(IdGenerator.genUuid(), IdGenerator.genUuid(), "bearer", oauth2AuthorizationServerClientInfo.getAccessTokenExpireTime(), authorizeInfo.getScope(), RyeCatcher.getLoginId(), oauth2AuthorizationServerClientInfo.getResourceIds(), oauth2AuthorizationServerClientInfo.getRefreshTokenExpireTime());
                tokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
                response.sendRedirect(authorizeInfo.getRedirectUri() + "?token=" + tokenInfo.getAccessToken() + "&state=" + authorizeInfo.getState());
            } else {
                throw new OAuth2Exception("invalid_response_type");
            }
        } else {
            getSession().setAttribute("authorizeInfo", authorizeInfo);
            response.sendRedirect(confirmPath);
        }
    }

    private Session getSession() {
        return SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found"));
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    static class CodeInfo {
        private final String code;
        private final String userId;
        private final String clientId;
        private final String redirectUri;
        private final String scope;
        private final LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);
    }


}
