package org.bitmagic.lab.reycatcher.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.oauth2.model.AuthorizeInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.ConfirmAccessInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
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
    private final OAuth2ConfigurationInfo oAuth2ConfigurationInfo;
    private final OAuth2TokenStore tokenStore;
    private final OAuth2ApprovalStore approvalStore;
    private final String loginPath;
    private final String confirmPath;

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

    {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            CODE_INFO_REPO.entrySet().removeIf(entry -> entry.getValue().expireTime.isBefore(now));
        }, 0, 30, TimeUnit.SECONDS);
    }


    // 登录 不做

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
            OAuth2ClientInfo oauth2ClientInfo = oAuth2ConfigurationInfo.getOauth2ClientInfo(authorizeInfo.getClientId());
            try {
                authorize0(authorizeInfo, response, oauth2ClientInfo);
            } catch (OAuth2Exception e) {
                log.warn("authorize error", e);
                response.sendRedirect(authorizeInfo.getRedirectUri() + "?error=" + e.getMessage() + "&state=" + authorizeInfo.getState());
            }
        }
    }

    public void confirmAccess(ConfirmAccessInfo confirmAccessInfo, HttpServletResponse response) throws IOException {
        AuthorizeInfo authorizeInfo = (AuthorizeInfo) getSession().getAttribute("authorizeInfo");
        OAuth2ClientInfo oauth2ClientInfo = oAuth2ConfigurationInfo.getOauth2ClientInfo(authorizeInfo.getClientId());
        try {
            tryOauth2Exception(!confirmAccessInfo.isApproval(), "invalid_approval");
            OAuth2Approval auth2Approval = OAuth2Approval.of(RyeCatcher.getLoginId(), authorizeInfo.getClientId(), authorizeInfo.getScope(), null, LocalDateTime.now().plusYears(1), LocalDateTime.now(), LocalDateTime.now());
            approvalStore.addApproval(auth2Approval);
            authorize0(authorizeInfo, response, oauth2ClientInfo);
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
    public Oauth2Token getAccessToken(RequestTokenInfo requestTokenInfo) {
        // check client_id,  redirect_uri, code, redirectUri
        String code = requestTokenInfo.getCode();
        CodeInfo codeInfo = CODE_INFO_REPO.get(code);
        tryOauth2Exception(Objects.isNull(codeInfo), "invalid_code");
        OAuth2ClientInfo oauth2ClientInfo = oAuth2ConfigurationInfo.getOauth2ClientInfo(requestTokenInfo.getClientId());
        tryOauth2Exception(Objects.isNull(oauth2ClientInfo), "invalid_client");
        tryOauth2Exception(!(oauth2ClientInfo.getRedirectUri().equals(requestTokenInfo.getRedirectUri()) || codeInfo.getRedirectUri().equals(requestTokenInfo.getRedirectUri())), "invalid_redirect_uri");
        tryOauth2Exception(oauth2ClientInfo.getClientSecret() != null && !oauth2ClientInfo.getClientSecret().equals(requestTokenInfo.getClientSecret()), "invalid_client_secret");
        tryOauth2Exception(!oauth2ClientInfo.getGrantTypes().contains(requestTokenInfo.getGrantType()), "invalid_grant_type");
        Oauth2Token tokenInfo = Oauth2Token.of(IdGenerator.genUuid(), IdGenerator.genUuid(), "bearer", oauth2ClientInfo.getAccessTokenExpireTime(), codeInfo.getScope(), codeInfo.getUserId());
        tokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }

    /**
     * refresh_token -> 换取 access_token
     *
     * @param refreshToken
     * @return
     */
    public Oauth2Token refreshAccessToken(String refreshToken) {
        Oauth2Token oldTokenInfo = tokenStore.getTokenInfoByRefreshToken(refreshToken);
        tryOauth2Exception(Objects.isNull(oldTokenInfo), "invalid_refresh_token");
        tokenStore.removeToken(oldTokenInfo.getAccessToken());
        Oauth2Token tokenInfo = Oauth2Token.of(IdGenerator.genUuid(), refreshToken, "bearer", oldTokenInfo.getExpiresIn(), null, oldTokenInfo.getUserId());
        tokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }


    public void checkToken(String accessToken) {
        tryOauth2Exception(Objects.isNull(tokenStore.getTokenInfo(accessToken)), "invalid_access_token");
    }

    public void revokeToken(String accessToken) {
        tokenStore.removeToken(accessToken);
    }

    private void authorize0(AuthorizeInfo authorizeInfo, HttpServletResponse response, OAuth2ClientInfo oauth2ClientInfo) throws IOException {
        // check client_id,  redirect_uri, response_type, scope
        tryOauth2Exception(Objects.isNull(oauth2ClientInfo), "invalid_client");
        if (approvalStore.containsApproval(authorizeInfo.getClientId(), RyeCatcher.getLoginId(), authorizeInfo.getScope())) {
            tryOauth2Exception(!oauth2ClientInfo.getScopes().contains(authorizeInfo.getScope()), "invalid_scope");
            tryOauth2Exception(!oauth2ClientInfo.getRedirectUri().equals(authorizeInfo.getRedirectUri()), "invalid_redirect_uri");
            tryOauth2Exception(!"code".equals(authorizeInfo.getResponseType()), "invalid_scope");
            tryOauth2Exception(!oauth2ClientInfo.getGrantTypes().contains("authorization_code"), "unsupported_grant_type");
            String userId = RyeCatcher.getLoginId();
            String code = IdGenerator.genUuid();
            CODE_INFO_REPO.put(code, CodeInfo.of(code, userId, oauth2ClientInfo.getClientId(), oauth2ClientInfo.getRedirectUri(), authorizeInfo.getScope()));
            response.sendRedirect(authorizeInfo.getRedirectUri() + "?code=" + code + "&state=" + authorizeInfo.getState());
        } else {
            getSession().setAttribute("authorizeInfo", authorizeInfo);
            response.sendRedirect(confirmPath);
        }

    }

    private Session getSession() {
        return SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found"));
    }


}
