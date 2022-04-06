package org.bitmagic.lab.reycatcher.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2AuthorizationServer;
import org.bitmagic.lab.reycatcher.oauth2.model.AuthorizeInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.ConfirmAccessInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.RequestTokenInfo;
import org.bitmagic.lab.reycatcher.oauth2.model.ResponseErrorInfo;
import org.bitmagic.lab.reycatcher.oauth2.support.HttpRequestParserUtils;
import org.bitmagic.lab.reycatcher.oauth2.support.OAuth2Exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangrd
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class OAuth2AuthorizationController {

    private final OAuth2AuthorizationServer server;

    @PostMapping("/authorize")
    public void authorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameterMap().containsKey("approval")) {
            ConfirmAccessInfo confirmAccessInfo = HttpRequestParserUtils.parseConfirmAccessRequest(request);
            server.confirmAccess(confirmAccessInfo, response);
        } else {
            AuthorizeInfo authorizeInfo = HttpRequestParserUtils.parseAuthorizeRequest(request);
            server.authorize(authorizeInfo, response);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(HttpServletRequest request) {
        try {
            RequestTokenInfo requestTokenInfo = HttpRequestParserUtils.parseRequestTokenRequest(request);
            return ResponseEntity.ok(server.getOauth2Token(requestTokenInfo));
        } catch (OAuth2Exception e) {
            return ResponseEntity.badRequest().body(ResponseErrorInfo.builder().error(e.getMessage()).build());
        }
    }

    @RequestMapping("/check-token")
    public void checkToken(String accessToken) {
        server.checkToken(accessToken);
    }

    @RequestMapping("/revoke-token")
    public void revokeToken(String accessToken) {
        server.revokeToken(accessToken);
    }

}
