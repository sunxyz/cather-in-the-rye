package org.bitmagic.lab.reycatcher.oauth2.support;

import org.bitmagic.lab.reycatcher.oauth2.model.AuthorizeInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.ConfirmAccessInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;

import javax.servlet.ServletRequest;

/**
 * @author yangrd
 */
public class HttpRequestParserUtils {

    /**
     * ServletRequest -> AuthorizeInfo
     *
     * @param request
     * @return
     */
    public static AuthorizeInfo parseAuthorizeRequest(ServletRequest request) {
        AuthorizeInfo info = new AuthorizeInfo();
        info.setClientId(request.getParameter("client_id"));
        info.setRedirectUri(request.getParameter("redirect_uri"));
        info.setResponseType(request.getParameter("response_type"));
        info.setScope(request.getParameter("scope"));
        info.setState(request.getParameter("state"));
        return info;
    }

    /**
     * ServletRequest -> RequestTokenInfo
     *
     * @param request
     * @return
     */
    public static RequestTokenInfo parseRequestTokenRequest(ServletRequest request) {
        RequestTokenInfo info = new RequestTokenInfo();
        info.setClientId(request.getParameter("client_id"));
        info.setClientSecret(request.getParameter("client_secret"));
        info.setRedirectUri(request.getParameter("redirect_uri"));
        info.setCode(request.getParameter("code"));
        info.setGrantType(request.getParameter("scope"));
        return info;
    }

    /**
     * ServletRequest -> ConfirmAccessInfo
     * @param request
     * @return
     */
    public static ConfirmAccessInfo parseConfirmAccessRequest(ServletRequest request) {
        ConfirmAccessInfo info = new ConfirmAccessInfo();
        info.setApproval(Boolean.parseBoolean(request.getParameter("approval")));
        return info;
    }
}
