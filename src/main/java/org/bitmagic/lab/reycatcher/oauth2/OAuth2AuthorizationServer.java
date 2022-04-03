package org.bitmagic.lab.reycatcher.oauth2;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.oauth2.model.AuthorizeInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.ConfirmAccessInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.UserInfo;
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

import static org.bitmagic.lab.reycatcher.oauth2.support.OAuth2ExceptionUtils.tryOauth2Exception;

/**
 * @author yangrd
 */
@RequiredArgsConstructor
public class OAuth2AuthorizationServer {

    private static final Map<String, String> CODE2_USER_ID = new ConcurrentHashMap<>();
    private final OAuth2Configuration oAuth2Configuration;
    private final OAuth2TokenStore tokenStore;
    private final OAuth2ApprovalStore approvalStore;
    private final OAuth2UserInfoProvider userInfoProvider;
    private final String loginPath;
    private final String confirmPath;



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
            OAuth2ClientInfo oauth2ClientInfo = oAuth2Configuration.getOauth2ClientInfo(authorizeInfo.getClientId());
            authorize0(authorizeInfo, response, oauth2ClientInfo);
        }
    }

    public void confirmAccess(ConfirmAccessInfo confirmAccessInfo, HttpServletResponse response) throws IOException {
        AuthorizeInfo authorizeInfo = (AuthorizeInfo) getSession().getAttribute("authorizeInfo");
        OAuth2ClientInfo oauth2ClientInfo = oAuth2Configuration.getOauth2ClientInfo(authorizeInfo.getClientId());
        tryOauth2Exception(!confirmAccessInfo.isApproval(), "invalid_request", null, authorizeInfo.getState());
        OAuth2Approval auth2Approval = OAuth2Approval.of(RyeCatcher.getLoginId(), authorizeInfo.getClientId(), authorizeInfo.getScope(), null, LocalDateTime.now().plusYears(1), LocalDateTime.now(), LocalDateTime.now());
        approvalStore.addApproval(auth2Approval);
        authorize0(authorizeInfo, response, oauth2ClientInfo);
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
        String userId = CODE2_USER_ID.get(code);
        tryOauth2Exception(Objects.isNull(userId), "invalid_code");
        OAuth2ClientInfo oauth2ClientInfo = oAuth2Configuration.getOauth2ClientInfo(requestTokenInfo.getClientId());
        tryOauth2Exception(Objects.isNull(oauth2ClientInfo), "invalid_client");
        tryOauth2Exception(!oauth2ClientInfo.getRedirectUri().equals(requestTokenInfo.getRedirectUri()), "invalid_redirect_uri");
        tryOauth2Exception(oauth2ClientInfo.getClientSecret() != null && !oauth2ClientInfo.getClientSecret().equals(requestTokenInfo.getClientSecret()), "invalid_client_secret");
        tryOauth2Exception(!oauth2ClientInfo.getGrantTypes().contains(requestTokenInfo.getGrantType()), "invalid_grant_type");
        Oauth2Token tokenInfo = Oauth2Token.of(IdGenerator.genUuid(), IdGenerator.genUuid(), "bearer", oauth2ClientInfo.getAccessTokenExpireTime(), null, userId);
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

    /**
     * getUserInfo
     * @param accessToken
     * @return
     */
    public UserInfo getUserInfo(String accessToken) {
        Oauth2Token tokenInfo = tokenStore.getTokenInfo(accessToken);
        tryOauth2Exception(Objects.isNull(tokenInfo), "accessToken not found");
        return userInfoProvider.loadUserInfo(tokenInfo.getUserId()).orElseThrow(() -> new OAuth2Exception("user info not found", null, null, null));
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
            tryOauth2Exception(!oauth2ClientInfo.getScopes().contains(authorizeInfo.getScope()), "invalid_scope", authorizeInfo.getRedirectUri(), authorizeInfo.getState());
            tryOauth2Exception(!oauth2ClientInfo.getRedirectUri().equals(authorizeInfo.getRedirectUri()), "invalid_redirect_uri", authorizeInfo.getRedirectUri(), authorizeInfo.getState());
            tryOauth2Exception(!"code".equals(authorizeInfo.getResponseType()), "invalid_scope", authorizeInfo.getRedirectUri(), authorizeInfo.getState());
            tryOauth2Exception(!oauth2ClientInfo.getGrantTypes().contains("authorization_code"), "unsupported_grant_type", authorizeInfo.getRedirectUri(), authorizeInfo.getState());
            String userId = RyeCatcher.getLoginId();
            String code = IdGenerator.genUuid();
            CODE2_USER_ID.put(code, userId);
            response.sendRedirect(authorizeInfo.getRedirectUri() + "?code=" + code + "&state=" + authorizeInfo.getState());
        } else {
            getSession().setAttribute("authorizeInfo", authorizeInfo);
            response.sendRedirect(confirmPath);
        }

    }

    private Session getSession() {
        return SessionContextHolder.getContext().findSession().orElseThrow(() -> new OAuth2Exception("session not found", null, null, null));
    }


}
