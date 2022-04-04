package org.bitmagic.lab.reycatcher.oauth2.helper;

import org.bitmagic.lab.reycatcher.ReqTokenInfo;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.BearerException;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;

public class RcOAuth2Helper {

    private final static OAuth2ResourceServer oauth2ResourceServer = InstanceHolder.getInstance(OAuth2ResourceServer.class);

    public static String getUserId() {
        return getUserId(getCurrentAccessTokenStr());
    }

    public static String getUserId(String accessToken) {
        return oauth2ResourceServer.getUserId(accessToken);
    }

    public static String getCurrentAccessTokenStr() {
        ReqTokenInfo reqTokenInfo = TokenParseUtils.parseReqTokenInfo(RcRequestContextHolder.getContext().getRequest().getHeader("Authorization")).orElseThrow(() -> new BearerException("not found Authorization header"));
        return reqTokenInfo.getValue();
    }
}
