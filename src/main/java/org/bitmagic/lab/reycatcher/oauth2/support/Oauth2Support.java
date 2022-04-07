package org.bitmagic.lab.reycatcher.oauth2.support;

import org.bitmagic.lab.reycatcher.oauth2.OAuth2ClientInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.AuthorizeInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangrd
 */
public class Oauth2Support {

    public static void redirectToLogin(HttpServletResponse response, OAuth2ClientInfo oAuth2ClientInfo, String state) throws IOException {
        String redirectUrl = oAuth2ClientInfo.getAuthorizationUri() + "?client_id=" + oAuth2ClientInfo.getClientId() + "&response_type=code&redirect_uri=" + oAuth2ClientInfo.getRedirectUri()+"&scope=" + String.join(",",oAuth2ClientInfo.getScope()) + "&state=" + state;
        response.sendRedirect(redirectUrl);
    }

    public static void redirectToAuthorize() throws IOException {
        redirectToAuthorize(RcRequestContextHolder.getContext().getRequest(), RcRequestContextHolder.getContext().getResponse());
    }

    public static void redirectToAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(getAuthorizationUrl(request));
    }

    public static String getAuthorizationUrl(AuthorizeInfo authorizeInfo) {
        return String.format("/oauth/authorize?client_id=%s&response_type=%s&redirect_uri=%s&scope=%s&state=%s", authorizeInfo.getClientId(), authorizeInfo.getResponseType(), authorizeInfo.getRedirectUri(), authorizeInfo.getScope(), authorizeInfo.getState());
    }

    public static String getAuthorizationUrl(HttpServletRequest request) {
        return getAuthorizationUrl(HttpRequestParserUtils.parseAuthorizeRequest(request));
    }

    public static String getTokenUrlParams(HttpServletRequest request) {
        return getTokenUrlParams(HttpRequestParserUtils.parseRequestTokenRequest(request));
    }

    public static String getTokenUrlParams(RequestTokenInfo request) {
        return String.format("?client_id=%s&client_secret=%s&grant_type=%s&redirect_uri=%s&code=%s", request.getClientId(), request.getClientSecret(), request.getGrantType(), request.getRedirectUri(), request.getCode());
    }
}
