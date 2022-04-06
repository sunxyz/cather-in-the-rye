package org.bitmagic.lab.reycatcher.oauth2.helper;

import org.bitmagic.lab.reycatcher.ReqTokenInfo;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.BearerException;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;

/**
 * @author yangrd
 */
public class RcOauth2ResourceHelper {

    private final static OAuth2ResourceServer OAUTH2_RESOURCE_SERVER = InstanceHolder.getInstance(OAuth2ResourceServer.class);

    public static String getUserId() {
        return getUserId(getCurrentAccessTokenStr());
    }

    public static String getUserId(String accessToken) {
        return OAUTH2_RESOURCE_SERVER.getUserId(accessToken);
    }

    public static String getCurrentAccessTokenStr() {
        ReqTokenInfo reqTokenInfo = TokenParseUtils.parseReqTokenInfo(RcRequestContextHolder.getContext().getRequest().getHeader("Authorization")).orElseThrow(() -> new BearerException("not found Authorization header"));
        return reqTokenInfo.getValue();
    }
}
