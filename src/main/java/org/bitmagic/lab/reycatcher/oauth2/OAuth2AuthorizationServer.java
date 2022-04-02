package org.bitmagic.lab.reycatcher.oauth2;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.oauth2.model.AuthorizeInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.ConfirmAccessInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.Oauth2TokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.support.OAuth2Exception;
import org.bitmagic.lab.reycatcher.support.SessionContextHolder;
import org.bitmagic.lab.reycatcher.utils.IdGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangrd
 */
@RequiredArgsConstructor
public class OAuth2AuthorizationServer {

    private static final Map<String, String> CODE2_USER_ID = new ConcurrentHashMap<>();
    private static final Map<String, String> GRANT_2_SCOPE = new ConcurrentHashMap<>();
    private final OAuth2Configuration oAuth2Configuration;
    private final OAuth2TokenStore oAuth2TokenStore;
    private Map<String, String> pathMappings = new ConcurrentHashMap<>();

    {
        pathMappings.put("/oauth/login", "/oauth/login");//1
//        pathMappings.put("/oauth/authorize", "/oauth/authorize");
//        pathMappings.put("/oauth/token", "/oauth/token");
//        pathMappings.put("/oauth/check_token", "/oauth/check_token");
//        pathMappings.put("/oauth/revoke_token", "/oauth/revoke_token");
//        pathMappings.put("/oauth/refresh_token", "/oauth/refresh_token");
        pathMappings.put("/oauth/confirm_access", "/oauth/confirm_access");//1
        pathMappings.put("/oauth/error", "/oauth/error");//1
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
            response.sendRedirect(pathMappings.get("/oauth/login") + String.format("?clientId=%s&redirectUri=%s&responseType=%s&scope=%s&state=%s", authorizeInfo.getClientId(), authorizeInfo.getRedirectUri(), authorizeInfo.getResponseType(), authorizeInfo.getScope(), authorizeInfo.getState()));
        } else {
            OAuth2ClientInfo oauth2ClientInfo = oAuth2Configuration.getOauth2ClientInfo(authorizeInfo.getClientId());
            String error = null;
            boolean isAuthorized = true;
            // check client_id,  redirect_uri, response_type, scope
            if (oauth2ClientInfo == null) {
                isAuthorized = false;
                error = "invalid_client";
            } else {
                SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found")).setAttribute("oAuth2ClientInfo", oauth2ClientInfo);
                if (!GRANT_2_SCOPE.containsKey("scope:" + oauth2ClientInfo.getClientId() + ":" + RyeCatcher.getLoginId())) {
                    SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found")).setAttribute("authorizeInfo", authorizeInfo);
                    response.sendRedirect(pathMappings.get("/oauth/confirm_access"));
                    return;
                } else if (!oauth2ClientInfo.getScopes().contains(authorizeInfo.getScope())) {
                    isAuthorized = false;
                    error = "invalid_redirect_uri";
                } else if (!oauth2ClientInfo.getRedirectUri().equals(authorizeInfo.getRedirectUri())) {
                    isAuthorized = false;
                    error = "invalid_redirect_uri";
                } else if (!"code".equals(authorizeInfo.getResponseType())) {
                    isAuthorized = false;
                    error = "invalid_response_type";
                } else if (!oauth2ClientInfo.getGrantTypes().contains("authorization_code")) {
                    isAuthorized = false;
                    error = "unsupported_grant_type";
                }
            }
            if (isAuthorized) {
                String userId = RyeCatcher.getLoginId();
                String code = IdGenerator.genUuid();
                CODE2_USER_ID.put(code, userId);
                response.sendRedirect(authorizeInfo.getRedirectUri() + "?code=" + code + "&state=" + authorizeInfo.getState());
            } else {
                if (pathMappings.containsKey("/oauth/error")) {
                    response.sendRedirect(pathMappings.get("/oauth/error") + "?redirect_uri" + authorizeInfo.getRedirectUri() + "&error=" + error + "&error_description=error_description&state=" + authorizeInfo.getState());
                } else {
                    response.sendRedirect(authorizeInfo.getRedirectUri() + "?error=" + error + "&error_description=error_description&state=" + authorizeInfo.getState());
                }
            }
        }
    }

    public void confirmAccess(ConfirmAccessInfo confirmAccessInfo, HttpServletResponse response) throws IOException {
        AuthorizeInfo authorizeInfo = (AuthorizeInfo) SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found")).getAttribute("authorizeInfo");
        OAuth2ClientInfo oauth2ClientInfo = oAuth2Configuration.getOauth2ClientInfo(authorizeInfo.getClientId());
        if (authorizeInfo == null) {
            response.sendRedirect(pathMappings.get("/oauth/error") + "?error=invalid_request&error_description=invalid_request&state=" + authorizeInfo.getState());
        } else if (confirmAccessInfo.isApproval()) {
            GRANT_2_SCOPE.put("scope:" + authorizeInfo.getClientId() + ":" + RyeCatcher.getLoginId(), authorizeInfo.getScope());
            String error = null;
            boolean isAuthorized = true;
            // check client_id,  redirect_uri, response_type, scope
            if (oauth2ClientInfo == null) {
                isAuthorized = false;
                error = "invalid_client";
            } else {
                SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found")).setAttribute("oAuth2ClientInfo", oauth2ClientInfo);
                if (!GRANT_2_SCOPE.containsKey("scope:" + oauth2ClientInfo.getClientId() + ":" + RyeCatcher.getLoginId())) {
                    SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found")).setAttribute("authorizeInfo", authorizeInfo);
                    response.sendRedirect(pathMappings.get("/oauth/confirm_access"));
                    return;
                } else if (!oauth2ClientInfo.getScopes().contains(authorizeInfo.getScope())) {
                    isAuthorized = false;
                    error = "invalid_redirect_uri";
                } else if (!oauth2ClientInfo.getRedirectUri().equals(authorizeInfo.getRedirectUri())) {
                    isAuthorized = false;
                    error = "invalid_redirect_uri";
                } else if (!"code".equals(authorizeInfo.getResponseType())) {
                    isAuthorized = false;
                    error = "invalid_response_type";
                } else if (!oauth2ClientInfo.getGrantTypes().contains("authorization_code")) {
                    isAuthorized = false;
                    error = "unsupported_grant_type";
                }
            }
            if (isAuthorized) {
                String userId = RyeCatcher.getLoginId();
                String code = IdGenerator.genUuid();
                CODE2_USER_ID.put(code, userId);
                response.sendRedirect(authorizeInfo.getRedirectUri() + "?code=" + code + "&state=" + authorizeInfo.getState());
            } else {
                if (pathMappings.containsKey("/oauth/error")) {
                    response.sendRedirect(pathMappings.get("/oauth/error") + "?redirect_uri" + authorizeInfo.getRedirectUri() + "&error=" + error + "&error_description=error_description&state=" + authorizeInfo.getState());
                } else {
                    response.sendRedirect(authorizeInfo.getRedirectUri() + "?error=" + error + "&error_description=error_description&state=" + authorizeInfo.getState());
                }
            }
        }
    }

    /**
     * code -> 换取 access_token
     *
     * @param requestTokenInfo
     * @return
     */
    public Oauth2TokenInfo getAccessToken(RequestTokenInfo requestTokenInfo) {
        // check client_id,  redirect_uri, code, redirectUri
        String code = requestTokenInfo.getCode();
        String userId = CODE2_USER_ID.get(code);
        if (userId == null) {
            throw new OAuth2Exception("invalid_code");
        }
        OAuth2ClientInfo oauth2ClientInfo = oAuth2Configuration.getOauth2ClientInfo(requestTokenInfo.getClientId());
        if (oauth2ClientInfo == null) {
            throw new OAuth2Exception("invalid_client");
        } else if (!oauth2ClientInfo.getRedirectUri().equals(requestTokenInfo.getRedirectUri())) {
            throw new OAuth2Exception("invalid_redirect_uri");
        } else if (oauth2ClientInfo.getClientSecret() != null && !oauth2ClientInfo.getClientSecret().equals(requestTokenInfo.getClientSecret())) {
            throw new OAuth2Exception("invalid_client_secret");
        }
        Oauth2TokenInfo tokenInfo = Oauth2TokenInfo.of(IdGenerator.genUuid(), IdGenerator.genUuid(), "bearer", oauth2ClientInfo.getAccessTokenExpireTime(), null, userId);
        oAuth2TokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }

    /**
     * refresh_token -> 换取 access_token
     *
     * @param refreshToken
     * @return
     */
    public Oauth2TokenInfo refreshAccessToken(String refreshToken) {
        Oauth2TokenInfo oldTokenInfo = oAuth2TokenStore.getTokenInfoByRefreshToken(refreshToken);
        if (oldTokenInfo == null) {
            throw new OAuth2Exception("invalid_refresh_token");
        }
        oAuth2TokenStore.removeToken(oldTokenInfo.getAccessToken());
        Oauth2TokenInfo tokenInfo = Oauth2TokenInfo.of(IdGenerator.genUuid(), refreshToken, "bearer", oldTokenInfo.getExpiresIn(), null, oldTokenInfo.getUserId());
        oAuth2TokenStore.storeToken(tokenInfo.getAccessToken(), tokenInfo);
        return tokenInfo;
    }


    public void checkToken(String accessToken) {
        if (Objects.isNull(oAuth2TokenStore.getTokenInfo(accessToken))) {
            throw new OAuth2Exception("invalid_access_token");
        }
    }

    public void revokeToken(String accessToken) {
        oAuth2TokenStore.removeToken(accessToken);
    }

}
